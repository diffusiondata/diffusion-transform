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

package com.pushtechnology.diffusion.transform.messaging.receive;

import java.util.concurrent.CompletableFuture;

import com.pushtechnology.diffusion.client.callbacks.Registration;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Builder for {@link TransformedRequestStream} that has not been bound to a session.
 *
 * @param <T> the type of request understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
public interface UnboundRequestReceiverBuilder<T, U, V> extends RequestReceiverBuilder<U, V> {

    @Override
    <R> UnboundRequestReceiverBuilder<T, R, V> transformRequest(UnsafeTransformer<U, R> newTransformer);

    @Override
    <R> UnboundRequestReceiverBuilder<T, U, R> transformResponse(UnsafeTransformer<R, V> newTransformer);

    /**
     * Bind the stream that will be built.
     * @param session the session to bind to
     * @return a new builder that creates streams for a session
     */
    BoundRequestReceiverBuilder<T, U, V> bind(Session session);

    /**
     * Register a request stream.
     *
     * @param session the session to register the stream with
     * @param selector the topic selector to match the stream
     * @param stream the stream handler
     */
    void setStream(Session session, String selector, TransformedRequestStream<T, U, V> stream);

    /**
     * Register a request handler.
     *
     * @param session the session to register the handler with
     * @param selector the topic selector to match the stream
     * @param handler the request handler
     * @param properties the session properties to receive with the request
     * @return the registration of the handler
     */
    CompletableFuture<Registration> addRequestHandler(
        Session session,
        String selector,
        TransformedRequestHandler<T, U, V> handler,
        String... properties);
}
