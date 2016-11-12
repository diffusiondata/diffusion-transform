/*******************************************************************************
 * Copyright (C) 2016 Push Technology Ltd.
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

import static com.pushtechnology.diffusion.transform.updater.UpdaterBuilders.updaterBuilder;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;
import com.pushtechnology.diffusion.transform.updater.TransformedUpdateSource;
import com.pushtechnology.diffusion.transform.updater.TransformedUpdater;

/**
 * A client that creates and updates JSON topics using an exclusive updater.
 *
 * @author Push Technology Limited
 */
public final class ProducingJsonWithExclusiveUpdater extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(ProducingJsonWithExclusiveUpdater.class);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private volatile Future<?> updateTask;

    /**
     * Constructor.
     * @param url The URL to connect to
     * @param principal The principal to connect as
     */
    public ProducingJsonWithExclusiveUpdater(String url, String principal) {
        super(url, principal);
    }

    @Override
    public void onConnected(Session session) {
        final JSON initialValue;
        try {
            initialValue = Transformers.parseJSON().transform("\"hello\"");
        }
        catch (TransformationException e) {
            LOG.error("Initial value could not be parsed as JSON");
            stop();
            return;
        }

        session
            .feature(TopicControl.class)
            .addTopicFromValue(
                "json/random",
                // This value cannot be transformed into a map, will invoke error handling if the client tries to
                // process it
                initialValue,
                new TopicControl.AddCallback.Default());

        updaterBuilder(JSON.class)
            .transform(Transformers.<RandomData>fromPojo())
            .register(
                session.feature(TopicUpdateControl.class),
                "json",
                new TransformedUpdateSource.Default<JSON, RandomData>() {
                    @Override
                    public void onActive(String topicPath, TransformedUpdater<JSON, RandomData> updater) {
                        updateTask = executor.scheduleAtFixedRate(
                            () -> {
                                try {
                                    updater.update(
                                        "json/random",
                                        RandomData.next(),
                                        new TopicUpdateControl.Updater.UpdateCallback.Default());
                                }
                                catch (TransformationException e) {
                                    LOG.warn("Failed to transform data", e);
                                }
                            },
                            0L,
                            1L,
                            SECONDS);
                    }
                });
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
        final ProducingJsonWithExclusiveUpdater client =
            new ProducingJsonWithExclusiveUpdater("ws://diffusion.example.com:80", "auth");
        client.start("auth_secret");
        client.waitForStopped();
    }
    // CHECKSTYLE.ON: UncommentedMain
}