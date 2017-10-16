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

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.types.ReceiveContext;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;

/**
 * Adapter from {@link Messaging.MessageStream} to {@link MessageStream}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
/*package*/ final class SafeMessageStreamAdapter<V> implements Messaging.MessageStream {
    private final SafeTransformer<Content, V> transformer;
    private final MessageStream<V> delegate;

    SafeMessageStreamAdapter(SafeTransformer<Content, V> transformer, MessageStream<V> delegate) {
        this.transformer = transformer;
        this.delegate = delegate;
    }

    @Override
    public void onMessageReceived(String path, Content content, ReceiveContext context) {
        delegate.onMessageReceived(path, transformer.transform(content));
    }

    @Override
    public void onClose() {
        delegate.onClose();
    }

    @Override
    public void onError(ErrorReason errorReason) {
        delegate.onError(errorReason);
    }
}
