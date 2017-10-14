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

import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Builder for {@link TransformedRequestStream} that has not been bound to a session.
 *
 * @param <T> the type of request understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
public interface UnboundRequestStreamBuilder<T, U, V> extends RequestStreamBuilder<U, V> {
    @Override
    <R> UnboundRequestStreamBuilder<T, R, V> transformRequest(Transformer<U, R> newTransformer);

    @Override
    <R> UnboundRequestStreamBuilder<T, R, V> transformRequestWith(UnsafeTransformer<U, R> newTransformer);

    @Override
    <R> UnboundRequestStreamBuilder<T, U, R> transformResponse(Transformer<R, V> newTransformer);

    @Override
    <R> UnboundRequestStreamBuilder<T, U, R> transformResponseWith(UnsafeTransformer<R, V> newTransformer);

    /**
     * Bind the stream that will be built.
     * @param session the session to bind to
     * @return a new builder that creates streams for a session
     */
    BoundRequestStreamBuilder<T, U, V> bind(Session session);

    /**
     * Register a request stream.
     *
     * @param session the session to register the stream with
     * @param selector the topic selector to match the stream
     * @param stream the stream handler
     */
    void setStream(Session session, String selector, TransformedRequestStream<T, U, V> stream);
}
