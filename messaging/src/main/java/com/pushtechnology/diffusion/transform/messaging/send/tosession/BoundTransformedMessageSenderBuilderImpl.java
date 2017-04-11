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

package com.pushtechnology.diffusion.transform.messaging.send.tosession;

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link BoundTransformedMessageSenderBuilder}.
 *
 * @param <V> the type of values
 * @author Matt Champion 11/04/2017
 */
/*package*/ final class BoundTransformedMessageSenderBuilderImpl<V> implements BoundTransformedMessageSenderBuilder<V> {
    private final MessagingControl messaging;
    private final Transformer<V, ? extends Bytes> transformer;

    /*package*/ BoundTransformedMessageSenderBuilderImpl(
            MessagingControl messaging,
            Transformer<V, ? extends Bytes> transformer) {
        this.messaging = messaging;
        this.transformer = transformer;
    }

    @Override
    public <R> BoundTransformedMessageSenderBuilder<R> transform(Transformer<R, V> newTransformer) {
        return new BoundTransformedMessageSenderBuilderImpl<>(messaging, chain(newTransformer, transformer));
    }

    @Override
    public <R> BoundTransformedMessageSenderBuilder<R> transformWith(UnsafeTransformer<R, V> newTransformer) {
        return transform(toTransformer(newTransformer));
    }

    @Override
    public MessageSender<V> build() {
        return new MessageSenderImpl<>(messaging, transformer);
    }
}
