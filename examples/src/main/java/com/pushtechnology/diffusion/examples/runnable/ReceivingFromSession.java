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

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.RequestHandler.RequestContext;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.messaging.receive.RequestReceiverBuilders;
import com.pushtechnology.diffusion.transform.messaging.receive.TransformedRequestHandler;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        RequestReceiverBuilders
            .requestStreamBuilder(JSON.class, String.class)
            .unsafeTransformRequest(Transformers.stringify().asUnsafeTransformer())
            .bind(session)
            .addRequestHandler(
                "json/random",
                new TransformedRequestHandler<JSON, String, String>() {
                    @Override
                    public void onRequest(
                        String request,
                        RequestContext requestContext,
                        Responder<String> responder) {

                        LOG.warn("{} request, path={}, message={}", this, requestContext.getPath(), request);
                        try {
                            responder.respond(null);
                        }
                        catch (TransformationException e) {
                            LOG.warn("{} failed to transform response", this, e);
                        }
                    }

                    @Override
                    public void onTransformationException(
                        JSON request,
                        RequestContext requestContext,
                        Responder<String> responder,
                        TransformationException e) {

                        LOG.warn(
                            "{} transformation error, path={}, message={}",
                            this,
                            requestContext.getPath(),
                            request,
                            e);
                    }

                    @Override
                    public void onClose() {
                        LOG.debug("{} closed", this);
                    }

                    @Override
                    public void onError(ErrorReason errorReason) {
                        LOG.warn("Failed to send message, {}", errorReason);
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
