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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link UnboundTransformedMessageReceiverBuilder}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 * @deprecated since 2.0.0 in favour of request receivers
 */
@Deprecated
/*package*/ final class UnboundTransformedMessageReceiverBuilderImpl<V> implements
    UnboundTransformedMessageReceiverBuilder<V> {

    private final Transformer<Content, V> transformer;

    UnboundTransformedMessageReceiverBuilderImpl(Transformer<Content, V> transformer) {
        this.transformer = transformer;
    }

    @Override
    public <R> UnboundTransformedMessageReceiverBuilder<R> transform(Transformer<V, R> newTransformer) {
        return new UnboundTransformedMessageReceiverBuilderImpl<>(chain(transformer, newTransformer));
    }

    @Override
    public <R> UnboundTransformedMessageReceiverBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer) {
        return new UnboundTransformedMessageReceiverBuilderImpl<>(chain(transformer, toTransformer(newTransformer)));
    }

    @Override
    public BoundTransformedMessageReceiverBuilder<V> bind(Session session) {
        return new BoundTransformedMessageReceiverBuilderImpl<>(transformer, session);
    }

    @Override
    public MessageReceiverHandle register(Session session, TransformedMessageStream<V> stream) {
        final Messaging.MessageStream adapter = new TransformedMessageStreamAdapter<>(transformer, stream);
        final Messaging messaging = session.feature(Messaging.class);
        final StreamHandle handle = new StreamHandle(messaging, adapter);
        messaging.addFallbackMessageStream(adapter);
        return handle;
    }

    @Override
    public MessageReceiverHandle register(Session session, String selector, TransformedMessageStream<V> stream) {
        final Messaging.MessageStream adapter = new TransformedMessageStreamAdapter<>(transformer, stream);
        final Messaging messaging = session.feature(Messaging.class);
        final StreamHandle handle = new StreamHandle(messaging, adapter);
        messaging.addMessageStream(selector, adapter);
        return handle;
    }

    @Override
    public MessageReceiverHandle register(
            Session session,
            String path,
            TransformedMessageHandler<V> handler,
            String... properties) {
        final HandlerHandle handle = new HandlerHandle();
        final TransformedMessageHandlerAdapter adapter =
            new TransformedMessageHandlerAdapter<>(transformer, handler, handle);
        final MessagingControl messagingControl = session.feature(MessagingControl.class);
        messagingControl.addMessageHandler(path, adapter, properties);
        return handle;
    }
}
