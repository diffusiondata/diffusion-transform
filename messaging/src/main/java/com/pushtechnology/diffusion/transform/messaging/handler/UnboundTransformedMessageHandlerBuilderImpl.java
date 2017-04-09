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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link UnboundTransformedMessageHandlerBuilder}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
/*package*/ final class UnboundTransformedMessageHandlerBuilderImpl<V> implements
    UnboundTransformedMessageHandlerBuilder<V> {

    private final Transformer<Content, V> transformer;

    UnboundTransformedMessageHandlerBuilderImpl(Transformer<Content, V> transformer) {
        this.transformer = transformer;
    }

    @Override
    public <R> UnboundTransformedMessageHandlerBuilder<R> transform(Transformer<V, R> newTransformer) {
        return new UnboundTransformedMessageHandlerBuilderImpl<>(chain(transformer, newTransformer));
    }

    @Override
    public <R> UnboundTransformedMessageHandlerBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer) {
        return new UnboundTransformedMessageHandlerBuilderImpl<>(chain(transformer, toTransformer(newTransformer)));
    }

    @Override
    public BoundTransformedMessageHandlerBuilder<V> bind(Session session) {
        return new BoundTransformedMessageHandlerBuilderImpl<>(transformer, session);
    }

    @Override
    public MessageHandlerHandle register(
            Session session,
            String path,
            TransformedMessageHandler<V> stream,
            String... properties) {
        final MessageHandlerHandleImpl handle = new MessageHandlerHandleImpl();
        final MessagingControl.MessageHandler adapter =
            new TransformedMessageHandlerAdapter<>(transformer, stream, handle);
        final MessagingControl messaging = session.feature(MessagingControl.class);
        messaging.addMessageHandler(path, adapter);
        return handle;
    }
}
