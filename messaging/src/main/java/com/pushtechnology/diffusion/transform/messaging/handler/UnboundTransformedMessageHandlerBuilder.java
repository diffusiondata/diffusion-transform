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

import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A builder for {@link TransformedMessageHandler}s that have not been bound to a session.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
public interface UnboundTransformedMessageHandlerBuilder<V> extends
    UnboundMessageHandlerBuilder<V, TransformedMessageHandler<V>>,
    TransformedMessageHandlerBuilder<V> {

    @Override
    <R> UnboundTransformedMessageHandlerBuilder<R> transform(Transformer<V, R> newTransformer);

    @Override
    <R> UnboundTransformedMessageHandlerBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer);

    @Override
    BoundTransformedMessageHandlerBuilder<V> bind(Session session);

    @Override
    MessageHandlerHandle register(
            Session session,
            String path,
            TransformedMessageHandler<V> stream,
            String... properties);
}
