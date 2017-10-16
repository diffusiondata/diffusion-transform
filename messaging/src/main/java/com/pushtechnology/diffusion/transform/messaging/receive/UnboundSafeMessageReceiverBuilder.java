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
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;

/**
 * A builder for safe message receivers that has not been bound to a session.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public interface UnboundSafeMessageReceiverBuilder<V> extends
    UnboundMessageReceiverBuilder<V, SafeMessageStream<V>, SafeMessageHandler<V>>,
    SafeMessageReceiverBuilder<V> {

    @Override
    <R> UnboundSafeMessageReceiverBuilder<R> transform(SafeTransformer<V, R> newTransformer);

    @Override
    BoundSafeMessageReceiverBuilder<V> bind(Session session);

    @Override
    MessageReceiverHandle register(Session session, SafeMessageStream<V> stream);

    @Override
    MessageReceiverHandle register(Session session, String selector, SafeMessageStream<V> stream);

    @Override
    MessageReceiverHandle register(Session session, String path, SafeMessageHandler<V> stream, String... properties);
}
