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

package com.pushtechnology.diffusion.transform.messaging.receive;

import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A builder for message receivers that has been bound to a session.
 *
 * @param <V> the type of values
 * @param <S> the type of stream
 * @param <H> the type of handler
 * @author Push Technology Limited
 */
public interface BoundMessageReceiverBuilder<V, S extends MessageStream<V>, H extends MessageHandler<V>>
        extends MessageReceiverBuilder<V> {
    @Override
    <R> BoundTransformedMessageReceiverBuilder<R> transform(Transformer<V, R> newTransformer);

    @Override
    <R> BoundTransformedMessageReceiverBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer);

    /**
     * Register a message stream. Stream receives all messages not passed to
     * sstreams registered with a selector.
     * @param stream the stream to register
     */
    MessageReceiverHandle register(S stream);

    /**
     * Register a message stream.
     * @param selector the selector for the paths to register the stream for.
     * @param stream the stream to register
     */
    MessageReceiverHandle register(String selector, S stream);

    /**
     * Register a message handler.
     * @param path the path to register interest in
     * @param handler the handler to register
     * @param properties the session properties to register interest in
     */
    MessageReceiverHandle register(String path, H handler, String... properties);
}
