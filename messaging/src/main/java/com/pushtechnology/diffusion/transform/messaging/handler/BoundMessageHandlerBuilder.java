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

package com.pushtechnology.diffusion.transform.messaging.handler;

import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A builder for {@link MessageHandler}s that have been bound to a session.
 *
 * @param <V> the type of values
 * @param <S> the type of stream
 * @author Push Technology Limited
 */
public interface BoundMessageHandlerBuilder<V, S extends MessageHandler<V>> extends MessageHandlerBuilder<V> {
    @Override
    <R> BoundTransformedMessageHandlerBuilder<R> transform(Transformer<V, R> newTransformer);

    @Override
    <R> BoundTransformedMessageHandlerBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer);

    /**
     * Register a message handler.
     * @param path the path to register interest in
     * @param stream the handler to register
     * @param properties the session properties to register interest in
     */
    MessageHandlerHandle register(String path, S stream, String... properties);
}
