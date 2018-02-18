/*******************************************************************************
 * Copyright (C) 2018 Push Technology Ltd.
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

import static com.pushtechnology.diffusion.client.topics.details.TopicSpecification.TIME_SERIES_EVENT_VALUE_TYPE;
import static com.pushtechnology.diffusion.client.topics.details.TopicType.TIME_SERIES;
import static com.pushtechnology.diffusion.transform.updater.UpdaterBuilders.updaterBuilder;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.Transformers;
import com.pushtechnology.diffusion.transform.updater.TimeSeriesUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client that creates and updates time series topics.
 *
 * @author Push Technology Limited
 */
public final class ProducingJsonTimeSeries extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(ProducingJsonTimeSeries.class);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private volatile Future<?> updateTask;

    /**
     * Constructor.
     * @param url The URL to connect to
     * @param principal The principal to connect as
     */
    public ProducingJsonTimeSeries(String url, String principal) {
        super(url, principal);
    }

    @Override
    public void onConnected(Session session) {
        final TopicControl topicControl = session.feature(TopicControl.class);
        topicControl
            .addTopic(
                "time/series/random",
                topicControl
                    .newSpecification(TIME_SERIES)
                    .withProperty(TIME_SERIES_EVENT_VALUE_TYPE, Diffusion.dataTypes().json().getTypeName()))
            .thenAccept(result -> beginUpdating(session))
            .exceptionally(ex -> {
                LOG.error("Failed to add topic time/series/random", ex);
                return null;
            });
    }

    private void beginUpdating(Session session) {
        final TimeSeriesUpdater<RandomData> valueUpdater = updaterBuilder(JSON.class)
            .unsafeTransform(Transformers.<RandomData>fromPojo())
            .createTimeSeries(session);

        updateTask = executor.scheduleAtFixedRate(
            () -> valueUpdater
                .append("time/series/random", RandomData.next())
                .exceptionally(e -> {
                    LOG.error("Failed to append data", e);
                    return null;
                }),
            0L,
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
        final ProducingJsonTimeSeries client =
            new ProducingJsonTimeSeries("ws://diffusion.example.com:80", "auth");
        client.start("auth_secret");
        client.waitForStopped();
    }
    // CHECKSTYLE.ON: UncommentedMain
}
