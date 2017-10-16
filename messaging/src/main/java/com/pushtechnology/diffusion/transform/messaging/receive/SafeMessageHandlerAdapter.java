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

package com.pushtechnology.diffusion.transform.messaging.receive;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.RegisteredHandler;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.client.types.ReceiveContext;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;

/**
 * Adapter from {@link MessagingControl.MessageHandler} to {@link MessageHandler}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
/*package*/ final class SafeMessageHandlerAdapter<V> implements MessagingControl.MessageHandler {
    private final SafeTransformer<Content, V> transformer;
    private final MessageHandler<V> delegate;
    private final HandlerHandle handle;

    SafeMessageHandlerAdapter(
            SafeTransformer<Content, V> transformer,
            MessageHandler<V> delegate,
            HandlerHandle handle) {
        this.transformer = transformer;
        this.delegate = delegate;
        this.handle = handle;
    }

    @Override
    public void onMessage(SessionId sessionId, String path, Content content, ReceiveContext context) {
        delegate.onMessageReceived(path, transformer.transform(content), sessionId, context.getSessionProperties());
    }

    @Override
    public void onActive(String path, RegisteredHandler registeredHandler) {
        handle.setHandle(registeredHandler);
        delegate.onRegistered(path);
    }

    @Override
    public void onClose(String path) {
        delegate.onClose(path);
    }
}
