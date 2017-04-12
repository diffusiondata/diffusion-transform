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

package com.pushtechnology.diffusion.transform.messaging;

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link UnboundSafeMessageStreamBuilder}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
/*package*/ final class UnboundSafeMessageStreamBuilderImpl<V> implements UnboundSafeMessageStreamBuilder<V> {
    private final SafeTransformer<Content, V> transformer;

    UnboundSafeMessageStreamBuilderImpl(SafeTransformer<Content, V> transformer) {
        this.transformer = transformer;
    }

    @Override
    public <R> UnboundTransformedMessageStreamBuilder<R> transform(Transformer<V, R> newTransformer) {
        return new UnboundTransformedMessageStreamBuilderImpl<>(chain(transformer, newTransformer));
    }

    @Override
    public <R> UnboundTransformedMessageStreamBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer) {
        return new UnboundTransformedMessageStreamBuilderImpl<>(chain(transformer, toTransformer(newTransformer)));
    }

    @Override
    public <R> UnboundSafeMessageStreamBuilder<R> transform(SafeTransformer<V, R> newTransformer) {
        return new UnboundSafeMessageStreamBuilderImpl<>(chain(transformer, newTransformer));
    }

    @Override
    public BoundSafeMessageStreamBuilder<V> bind(Session session) {
        return new BoundSafeMessageStreamBuilderImpl<>(transformer, session);
    }

    @Override
    public MessageStreamHandle register(Session session, SafeMessageStream<V> stream) {
        final Messaging.MessageStream adapter = new SafeMessageStreamAdapter<>(transformer, stream);
        final Messaging messaging = session.feature(Messaging.class);
        final MessageStreamHandleImpl handle = new MessageStreamHandleImpl(messaging, adapter);
        messaging.addFallbackMessageStream(adapter);
        return handle;
    }
}
