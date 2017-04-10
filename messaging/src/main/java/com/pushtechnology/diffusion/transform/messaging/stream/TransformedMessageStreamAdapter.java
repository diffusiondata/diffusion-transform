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

package com.pushtechnology.diffusion.transform.messaging.stream;

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.types.ReceiveContext;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Adapter from {@link Messaging.MessageStream} to {@link TransformedMessageStream}.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
/*package*/ final class TransformedMessageStreamAdapter<V> implements Messaging.MessageStream {
    private final Transformer<Content, V> transformer;
    private final TransformedMessageStream<V> delegate;

    TransformedMessageStreamAdapter(Transformer<Content, V> transformer, TransformedMessageStream<V> delegate) {
        this.transformer = transformer;
        this.delegate = delegate;
    }

    @Override
    public void onMessageReceived(String path, Content content, ReceiveContext context) {
        try {
            delegate.onMessageReceived(path, transformer.transform(content));
        }
        catch (TransformationException e) {
            delegate.onTransformationException(path, content, e);
        }
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
