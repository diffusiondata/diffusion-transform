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
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public interface BoundTransformedMessageReceiverBuilder<V> extends
    BoundMessageReceiverBuilder<V, TransformedMessageStream<V>, TransformedMessageHandler<V>>,
    TransformedMessageReceiverBuilder<V> {

    @Override
    <R> BoundTransformedMessageReceiverBuilder<R> transform(Transformer<V, R> newTransformer);

    @Override
    <R> BoundTransformedMessageReceiverBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer);

    @Override
    MessageReceiverHandle register(TransformedMessageStream<V> stream);

    @Override
    MessageReceiverHandle register(String selector, TransformedMessageStream<V> stream);

    @Override
    MessageReceiverHandle register(String path, TransformedMessageHandler<V> handler, String... properties);
}
