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
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link BoundTransformedMessageSenderBuilder}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
/*package*/ final class BoundTransformedMessageSenderBuilderImpl<V> implements BoundTransformedMessageSenderBuilder<V> {
    private final Session session;
    private final Transformer<V, ? extends Bytes> transformer;

    /*package*/ BoundTransformedMessageSenderBuilderImpl(
            Session session,
            Transformer<V, ? extends Bytes> transformer) {
        this.session = session;
        this.transformer = transformer;
    }

    @Override
    public <R> BoundTransformedMessageSenderBuilder<R> transform(Transformer<R, V> newTransformer) {
        return new BoundTransformedMessageSenderBuilderImpl<>(session, chain(newTransformer, transformer));
    }

    @Override
    public <R> BoundTransformedMessageSenderBuilder<R> transformWith(UnsafeTransformer<R, V> newTransformer) {
        return transform(toTransformer(newTransformer));
    }

    @Override
    public MessageToSessionSender<V> buildToSessionSender() {
        return new MessageToSessionSenderImpl<>(session.feature(MessagingControl.class), transformer);
    }

    @Override
    public MessageToHandlerSender<V> buildToHandlerSender() {
        return new MessageToHandlerSenderImpl<>(session.feature(Messaging.class), transformer);
    }
}
