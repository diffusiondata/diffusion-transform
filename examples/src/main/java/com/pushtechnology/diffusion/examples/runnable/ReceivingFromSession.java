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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.transform.messaging.receive.MessageReceiverBuilders;
import com.pushtechnology.diffusion.transform.messaging.receive.TransformedMessageHandler;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * A client that receives JSON messages sent to a path.
 *
 * @author Push Technology Limited
 */
public final class ReceivingFromSession extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(ReceivingFromSession.class);

    /**
     * Constructor.
     *
     * @param url The URL to connect to
     * @param principal The principal to connect as
     */
    public ReceivingFromSession(String url, String principal) {
        super(url, principal);
    }

    @Override
    public void onStarted(Session session) {
        MessageReceiverBuilders
            .newJSONMessageReceiverBuilder()
            .transform(Transformers.stringify())
            .bind(session)
            .register(
                "json/random",
                new TransformedMessageHandler.Default<String>() {
                    @Override
                    public void onTransformationException(
                            String path,
                            Content value,
                            SessionId sessionId,
                            Map<String, String> sessionProperties,
                            TransformationException e) {
                        LOG.warn("{} transformation error, path={}, message={}", this, path, value, e);
                    }

                    @Override
                    public void onMessageReceived(
                            String path,
                            String message,
                            SessionId sessionId,
                            Map<String, String> sessionProperties) {
                        LOG.warn("{} message, path={}, message={}", this, path, message);
                    }
                });
    }

    /**
     * Entry point for the example.
     * @param args The command line arguments
     * @throws InterruptedException If the main thread was interrupted
     */
    // CHECKSTYLE.OFF: UncommentedMain
    public static void main(String[] args) throws InterruptedException {
        final ReceivingFromSession client =
            new ReceivingFromSession("ws://diffusion.example.com:80", "auth");
        client.start("auth_secret");
        client.waitForStopped();
    }
    // CHECKSTYLE.ON: UncommentedMain
}
