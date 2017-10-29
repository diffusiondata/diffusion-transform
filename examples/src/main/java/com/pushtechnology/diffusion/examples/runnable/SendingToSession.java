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

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.messaging.send.RequestSenderBuilders;
import com.pushtechnology.diffusion.transform.messaging.send.RequestToSessionSender;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client that sends JSON messages to a session filter.
 *
 * @author Push Technology Limited
 */
public final class SendingToSession extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(SendingToSession.class);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private volatile Future<?> updateTask;

    /**
     * Constructor.
     * @param url The URL to connect to
     * @param principal The principal to connect as
     */
    public SendingToSession(String url, String principal) {
        super(url, principal);
    }

    @Override
    public void onStarted(Session session) {
        final RequestToSessionSender<String, RandomData, String> sender = RequestSenderBuilders
            .requestSenderBuilder(JSON.class, String.class)
            .unsafeTransformRequest(Transformers.<RandomData>fromPojo())
            .bind(session)
            .buildToSessionSender();

        updateTask = executor.scheduleAtFixedRate(
            () -> {
                try {
                    sender.sendRequest(
                        "$Principal IS 'auth'",
                        "json/random",
                        RandomData.next(),
                        new RequestToSessionSender.TransformedFilterCallback<String, String>() {
                            @Override
                            public void onResponse(SessionId sessionId, String response) {
                                LOG.warn("Received response from {}", sessionId);
                            }

                            @Override
                            public void onResponseError(SessionId sessionId, Throwable t) {
                                LOG.warn("Failed to send message to session {}", sessionId, t);
                            }

                            @Override
                            public void onTransformationException(
                                    SessionId sessionId,
                                    String response,
                                    TransformationException e) {
                                LOG.warn("Failed to transform response from {}", sessionId, e);
                            }

                            @Override
                            public void onClose() {
                                LOG.info("All responses received");
                            }

                            @Override
                            public void onError(ErrorReason errorReason) {
                                LOG.warn("Failed to send message, {}", errorReason);
                            }
                        }).thenAccept(numberSent -> LOG.warn("Sent message to {} clients", numberSent));
                }
                catch (TransformationException e) {
                    LOG.warn("Failed to transform data", e);
                }
            },
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
        final SendingToSession client =
            new SendingToSession("ws://diffusion.example.com:80", "auth");
        client.start("auth_secret");
        client.waitForStopped();
    }
    // CHECKSTYLE.ON: UncommentedMain
}
