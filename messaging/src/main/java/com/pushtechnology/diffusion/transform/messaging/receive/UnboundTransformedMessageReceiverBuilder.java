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
 * A builder for transformed message receivers that has not been bound to a session.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 * @deprecated since 2.0.0 in favour of request receivers
 */
@Deprecated
public interface UnboundTransformedMessageReceiverBuilder<V> extends
    UnboundMessageReceiverBuilder<V, TransformedMessageStream<V>, TransformedMessageHandler<V>>,
    TransformedMessageReceiverBuilder<V> {

    @Override
    <R> UnboundTransformedMessageReceiverBuilder<R> transform(Transformer<V, R> newTransformer);

    @Override
    <R> UnboundTransformedMessageReceiverBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer);

    @Override
    BoundTransformedMessageReceiverBuilder<V> bind(Session session);

    @Override
    MessageReceiverHandle register(Session session, TransformedMessageStream<V> stream);

    @Override
    MessageReceiverHandle register(Session session, String selector, TransformedMessageStream<V> stream);

    @Override
    MessageReceiverHandle register(
        Session session,
        String path,
        TransformedMessageHandler<V> stream,
        String... properties);
}
