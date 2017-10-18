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

import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Builder for {@link TransformedRequestStream} that has been bound to a session.
 *
 * @param <T> the type of request understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
public interface BoundRequestStreamBuilder<T, U, V> extends RequestStreamBuilder<U, V> {

    @Override
    <R> BoundRequestStreamBuilder<T, R, V> transformRequest(UnsafeTransformer<U, R> newTransformer);

    @Override
    <R> BoundRequestStreamBuilder<T, U, R> transformResponse(UnsafeTransformer<R, V> newTransformer);

    /**
     * Register a request stream.
     *
     * @param selector the topic selector to match the stream
     * @param stream the stream handler
     */
    void setStream(String selector, TransformedRequestStream<T, U, V> stream);
}
