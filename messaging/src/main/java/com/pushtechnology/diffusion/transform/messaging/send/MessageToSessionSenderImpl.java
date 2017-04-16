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

import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendCallback;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendToFilterCallback;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Implementation of {@link com.pushtechnology.diffusion.transform.messaging.send.tohandler.MessageSender}.
 *
 * @param <V> the type of values
 * @author Matt Champion 11/04/2017
 */
/*package*/ final class MessageToSessionSenderImpl<V> implements MessageToSessionSender<V> {
    private final MessagingControl messagingControl;
    private final Transformer<V, ? extends Bytes> transformer;

    /**
     * Constructor.
     */
    /*package*/ MessageToSessionSenderImpl(
            MessagingControl messagingControl,
            Transformer<V, ? extends Bytes> transformer) {
        this.messagingControl = messagingControl;
        this.transformer = transformer;
    }

    @Override
    public void send(SessionId sessionId, String path, V message, SendCallback sendCallback)
            throws TransformationException {
        messagingControl.send(sessionId, path, transformer.transform(message), sendCallback);
    }

    @Override
    public void sendToFilter(String sessionFilter, String path, V message, SendToFilterCallback sendCallback)
            throws TransformationException {
        messagingControl.sendToFilter(sessionFilter, path, transformer.transform(message), sendCallback);
    }
}
