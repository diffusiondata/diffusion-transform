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
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link BoundSafeMessageHandlerBuilder}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
/*package*/ class BoundSafeMessageHandlerBuilderImpl<V> implements BoundSafeMessageHandlerBuilder<V> {
    private final SafeTransformer<Content, V> transformer;
    private final Session session;

    BoundSafeMessageHandlerBuilderImpl(SafeTransformer<Content, V> transformer, Session session) {
        this.transformer = transformer;
        this.session = session;
    }

    @Override
    public <R> BoundSafeMessageHandlerBuilder<R> transform(SafeTransformer<V, R> newTransformer) {
        return new BoundSafeMessageHandlerBuilderImpl<>(chain(transformer, newTransformer), session);
    }

    @Override
    public MessageHandlerHandle register(String path, SafeMessageHandler<V> stream, String... properties) {
        final MessageHandlerHandleImpl handle = new MessageHandlerHandleImpl();
        final MessagingControl.MessageHandler adapter = new SafeMessageHandlerAdapter<>(transformer, stream, handle);
        final MessagingControl messaging = session.feature(MessagingControl.class);
        messaging.addMessageHandler(path, adapter, properties);
        return handle;
    }

    @Override
    public <R> BoundTransformedMessageHandlerBuilder<R> transform(Transformer<V, R> newTransformer) {
        return new BoundTransformedMessageHandlerBuilderImpl<>(chain(transformer, newTransformer), session);
    }

    @Override
    public <R> BoundTransformedMessageHandlerBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer) {
        return new BoundTransformedMessageHandlerBuilderImpl<>(
            chain(transformer, toTransformer(newTransformer)),
            session);
    }
}
