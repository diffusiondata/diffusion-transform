/*******************************************************************************
 * Copyright (C) 2017 Push Technology Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.pushtechnology.diffusion.examples.runnable;

import static com.fasterxml.jackson.databind.DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS;
import static com.pushtechnology.diffusion.transform.updater.UpdaterBuilders.updaterBuilder;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.JSONTransformers;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.updater.TransformedUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A client that creates and updates JSON topics containing a timestamp as an array of numbers.
 * <p>
 * Uses a Jackson module to format the timestamp.
 *
 * @author Push Technology Limited
 */
public final class ProducingJsonTimestamp extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(ProducingJsonTimestamp.class);
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    private volatile Future<?> updateTask;

    /**
     * Constructor.
     * @param url The URL to connect to
     * @param principal The principal to connect as
     */
    public ProducingJsonTimestamp(String url, String principal) {
        super(url, principal);
    }

    @Override
    public void onConnected(Session session) {
        session
            .feature(TopicControl.class)
            .addTopic("json/timestamp", TopicType.JSON)
            .thenAccept(result -> beginUpdating(session))
            .exceptionally(ex -> {
                LOG.error("Failed to add topic json/timestamp", ex);
                return null;
            });
    }

    private void beginUpdating(Session session) {
        LOG.debug("Begin updating topic");

        final TopicUpdateControl.Updater updater = session
            .feature(TopicUpdateControl.class)
            .updater();

        // Create an instance of JSONTransformers with a Jackson module to
        // handle the serialisation of the Java 8 time classes
        final JSONTransformers jsonTransformers = JSONTransformers
            .builder()
            .registerModule(new JavaTimeModule())
            .configure(WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .build();

        // Create a one-way transforming value updater that cannot be used to lookup cached values
        final TransformedUpdater<JSON, LocalDateTime> valueUpdater = updaterBuilder(JSON.class)
            .unsafeTransform(jsonTransformers.<LocalDateTime>fromPojo())
            .create(updater);

        updateTask = EXECUTOR.scheduleAtFixedRate(
            () -> {
                try {
                    valueUpdater.update(
                        "json/timestamp",
                        LocalDateTime.now(),
                        new TopicUpdateControl.Updater.UpdateCallback.Default());
                }
                catch (TransformationException e) {
                    LOG.warn("Failed to transform data", e);
                }
            },
            1L,
            1L,
            SECONDS);
    }

    @Override
    public void onDisconnected() {
        updateTask.cancel(false);
    }

    @Override
    public void onError(ErrorReason errorReason) {
        LOG.error("Failed to start client: {}", errorReason);
    }

    /**
     * Entry point for the example.
     * @param args The command line arguments
     * @throws InterruptedException If the main thread was interrupted
     */
    // CHECKSTYLE.OFF: UncommentedMain
    public static void main(String[] args) throws InterruptedException {
        final ProducingJsonTimestamp client =
            new ProducingJsonTimestamp("ws://diffusion.example.com:80", "auth");
        client.start("auth_secret");
        client.waitForStopped();
    }
    // CHECKSTYLE.ON: UncommentedMain
}
