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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.messaging.stream.MessageStreamBuilders;
import com.pushtechnology.diffusion.transform.messaging.stream.TransformedMessageStream;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * A client that receives JSON messages sent specifically to it.
 *
 * @author Matt Champion 13/04/2017
 */
public final class ReceivingFromServer extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(ReceivingFromServer.class);

    /**
     * Constructor.
     *
     * @param url The URL to connect to
     * @param principal The principal to connect as
     */
    public ReceivingFromServer(String url, String principal) {
        super(url, principal);
    }

    @Override
    public void onStarted(Session session) {
        MessageStreamBuilders
            .newMessageStreamBuilder()
            .transform(Transformers.toByteArray())
            .transform(bytes -> Diffusion.dataTypes().json().readValue(bytes))
            .transform(Transformers.stringify())
            .bind(session)
            .register(new TransformedMessageStream.Default<String>() {
                @Override
                public void onTransformationException(String path, Content value, TransformationException e) {
                    LOG.warn("{} transformation error, path={}, message={}", this, path, value, e);
                }

                @Override
                public void onMessageReceived(String path, String message) {
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
        final ReceivingFromServer client =
            new ReceivingFromServer("ws://diffusion.example.com:80", "auth");
        client.start("auth_secret");
        client.waitForStopped();
    }
    // CHECKSTYLE.ON: UncommentedMain
}
