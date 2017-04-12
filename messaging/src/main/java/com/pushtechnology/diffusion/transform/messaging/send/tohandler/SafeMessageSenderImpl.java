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

package com.pushtechnology.diffusion.transform.messaging.send.tohandler;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.features.Messaging.SendCallback;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;

/**
 * Implementation of {@link SafeMessageSender}.
 *
 * @param <V> the type of values
 * @author Matt Champion 11/04/2017
 */
/*package*/ final class SafeMessageSenderImpl<V> implements SafeMessageSender<V> {
    private final Messaging messaging;
    private final SafeTransformer<V, ? extends Bytes> transformer;

    /**
     * Constructor.
     */
    /*package*/ SafeMessageSenderImpl(Messaging messaging, SafeTransformer<V, ? extends Bytes> transformer) {
        this.messaging = messaging;
        this.transformer = transformer;
    }

    @Override
    public void send(String path, V message, SendCallback sendCallback) {
        messaging.send(path, transformer.transform(message), sendCallback);
    }
}
