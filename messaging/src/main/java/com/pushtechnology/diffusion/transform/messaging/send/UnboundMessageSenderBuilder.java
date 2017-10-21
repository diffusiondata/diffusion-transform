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

import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A builder for {@link MessageToSessionSender}s that has not been bound to a session.
 *
 * @param <V> the type of values
 * @param <T> the type of to session sender
 * @param <U> the type of to handler sender
 * @author Push Technology Limited
 * @deprecated since 2.0.0 in favour of request senders
 */
@Deprecated
public interface UnboundMessageSenderBuilder<V, T, U> extends MessageSenderBuilder<V> {
    @Override
    <R> UnboundTransformedMessageSenderBuilder<R> transform(Transformer<R, V> newTransformer);

    @Override
    <R> UnboundTransformedMessageSenderBuilder<R> transformWith(UnsafeTransformer<R, V> newTransformer);

    /**
     * Bind the sender that will be built.
     * @param session the session to bind to
     * @return a new builder that creates senders for a session
     */
    BoundMessageSenderBuilder<V, T, U> bind(Session session);

    /**
     * Create a message to session sender.
     * @param session the session to send from
     */
    T buildToSessionSender(Session session);

    /**
     * Create a message to handler sender.
     * @param session the session to send from
     */
    U buildToHandlerSender(Session session);
}
