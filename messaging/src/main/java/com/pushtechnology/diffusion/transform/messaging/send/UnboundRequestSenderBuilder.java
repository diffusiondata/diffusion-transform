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

package com.pushtechnology.diffusion.transform.messaging.send;

import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A builder for {@link RequestToHandlerSender}s that has not been bound to a session.
 *
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
public interface UnboundRequestSenderBuilder<U, V> extends RequestSenderBuilder<U, V> {
    @Override
    <R> UnboundRequestSenderBuilder<R, V> transformRequest(Transformer<R, U> newTransformer);

    @Override
    <R> UnboundRequestSenderBuilder<R, V> transformRequestWith(UnsafeTransformer<R, U> newTransformer);

    @Override
    <R> UnboundRequestSenderBuilder<U, R> transformResponse(Transformer<V, R> newTransformer);

    @Override
    <R> UnboundRequestSenderBuilder<U, R> transformResponseWith(UnsafeTransformer<V, R> newTransformer);

    /**
     * Bind the sender that will be built.
     * @param session the session to bind to
     * @return a new builder that creates senders for a session
     */
    BoundRequestSenderBuilder<U, V> bind(Session session);

    /**
     * Create a request to handler sender.
     * @param session the session to send from
     */
    RequestToHandlerSender<U, V> buildToHandlerSender(Session session);
}
