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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link UnboundTransformedMessageSenderBuilder}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class UnboundSafeMessageSenderBuilderImpl<V> implements UnboundSafeMessageSenderBuilder<V> {
    private final SafeTransformer<V, ? extends Bytes> transformer;

    /*package*/ UnboundSafeMessageSenderBuilderImpl(SafeTransformer<V, ? extends Bytes> transformer) {
        this.transformer = transformer;
    }

    @Override
    public <R> UnboundTransformedMessageSenderBuilder<R> transform(Transformer<R, V> newTransformer) {
        return new UnboundTransformedMessageSenderBuilderImpl<>(chain(newTransformer, transformer));
    }

    @Override
    public <R> UnboundTransformedMessageSenderBuilder<R> transformWith(UnsafeTransformer<R, V> newTransformer) {
        return transform(toTransformer(newTransformer));
    }

    @Override
    public <R> UnboundSafeMessageSenderBuilder<R> transform(SafeTransformer<R, V> newTransformer) {
        return new UnboundSafeMessageSenderBuilderImpl<>(chain(newTransformer, transformer));
    }

    @Override
    public BoundSafeMessageSenderBuilder<V> bind(Session session) {
        return new BoundSafeMessageSenderBuilderImpl<>(session, transformer);
    }

    @Override
    public SafeMessageToSessionSender<V> buildToSessionSender(Session session) {
        return new SafeMessageToSessionSenderImpl<>(session.feature(MessagingControl.class), transformer);
    }

    @Override
    public SafeMessageToHandlerSender<V> buildToHandlerSender(Session session) {
        return new SafeMessageToHandlerSenderImpl<>(session.feature(Messaging.class), transformer);
    }
}
