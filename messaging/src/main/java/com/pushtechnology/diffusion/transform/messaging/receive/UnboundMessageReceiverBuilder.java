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

import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A builder for message receivers that has not been bound to a session.
 *
 * @param <V> the type of values
 * @param <S> the type of stream
 * @param <H> the type of handler
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public interface UnboundMessageReceiverBuilder<V, S extends MessageStream<V>, H extends MessageHandler<V>>
        extends MessageReceiverBuilder<V> {
    @Override
    <R> UnboundTransformedMessageReceiverBuilder<R> transform(Transformer<V, R> newTransformer);

    @Override
    <R> UnboundTransformedMessageReceiverBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer);

    /**
     * Bind the stream or handler that will be built.
     * @param session the session to bind to
     * @return a new builder that creates handlers for a session
     */
    BoundMessageReceiverBuilder<V, S, H> bind(Session session);

    /**
     * Register a message stream. Stream receives all messages not passed to
     * sstreams registered with a selector.
     * @param session the session to register the stream with
     * @param stream the stream to register
     */
    MessageReceiverHandle register(Session session, S stream);

    /**
     * Register a message stream.
     * @param session the session to register the stream with
     * @param selector the selector for the paths to register the stream for.
     * @param stream the stream to register
     */
    MessageReceiverHandle register(Session session, String selector, S stream);

    /**
     * Register a message handler.
     * @param session the session to register the stream with
     * @param path the path to register interest in
     * @param handler the handler to register
     * @param properties the session properties to register interest in
     */
    MessageReceiverHandle register(Session session, String path, H handler, String... properties);
}
