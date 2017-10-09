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

import static com.pushtechnology.diffusion.transform.adder.TopicAdderBuilders.jsonTopicAdderBuilder;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.parseJSON;
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
import com.pushtechnology.diffusion.transform.adder.TopicAdder;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;
import com.pushtechnology.diffusion.transform.updater.BoundTransformedUpdaterBuilder;
import com.pushtechnology.diffusion.transform.updater.TransformedUpdateSource;
import com.pushtechnology.diffusion.transform.updater.TransformedUpdater;
import com.pushtechnology.diffusion.transform.updater.UnboundSafeUpdaterBuilder;
import com.pushtechnology.diffusion.transform.updater.UnboundTransformedUpdaterBuilder;

/**
 * A client that reuses the updater builders.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class ReusingUpdaterBuilders extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(ProducingJson.class);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final UnboundTransformedUpdaterBuilder<JSON, RandomData> updateBuilder;

    private volatile Future<?> updateTask;

    /**
     * Constructor.
     * @param url The URL to connect to
     * @param principal The principal to connect as
     * @param updateBuilder The update builder
     */
    public ReusingUpdaterBuilders(
            String url,
            String principal,
            UnboundTransformedUpdaterBuilder<JSON, RandomData> updateBuilder) {
        super(url, principal);
        this.updateBuilder = updateBuilder;
    }

    @Override
    public void onConnected(Session session) {
        final TopicAdder<String> adder = jsonTopicAdderBuilder()
            .transform(parseJSON())
            .bind(session)
            .create();

        try {
            adder.add("json/random", "{}", new TopicControl.AddCallback.Default());
            adder.add("other/random", "{}", new TopicControl.AddCallback.Default());
        }
        catch (TransformationException e) {
            LOG.error("Initial value could not be parsed as JSON");
            stop();
            return;
        }

        final BoundTransformedUpdaterBuilder<JSON, RandomData> builder = updateBuilder.bind(session);
        builder.register(
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

        builder.register(
            "other",
            new TransformedUpdateSource.Default<JSON, RandomData>() {
                @Override
                public void onActive(String topicPath, TransformedUpdater<JSON, RandomData> updater) {
                    updateTask = executor.scheduleAtFixedRate(
                        () -> {
                            try {
                                updater.update(
                                    "other/random",
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
        final UnboundSafeUpdaterBuilder<JSON, JSON> safeUpdateBuilder = updaterBuilder(JSON.class);
        final UnboundTransformedUpdaterBuilder<JSON, RandomData> updaterBuilder = safeUpdateBuilder
            .transform(Transformers.<RandomData>fromPojo());

        final ReusingUpdaterBuilders client0 =
            new ReusingUpdaterBuilders("ws://diffusion.example.com:80", "auth", updaterBuilder);
        client0.start("auth_secret");
        client0.waitForStopped();

        final ReusingUpdaterBuilders client1 =
            new ReusingUpdaterBuilders("ws://diffusion.example.com:80", "auth", updaterBuilder);
        client1.start("auth_secret");
        client1.waitForStopped();
    }
    // CHECKSTYLE.ON: UncommentedMain
}
