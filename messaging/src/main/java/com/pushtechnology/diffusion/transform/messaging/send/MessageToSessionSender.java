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

import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendCallback;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendToFilterCallback;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * A sender of values as messages. May transform them before sending.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 * @deprecated since 2.0.0 in favour of request senders
 */
@Deprecated
public interface MessageToSessionSender<V> {

    /**
     * Send a message.
     *
     * @param sessionId the id of the session to send to
     * @param path the path to send the message to
     * @param message the message
     * @param sendCallback the callback for when the send has completed
     */
    void send(SessionId sessionId, String path, V message, SendCallback sendCallback) throws TransformationException;

    /**
     * Send messages to sessions matching filter.
     *
     * @param sessionFilter filter of sessions
     * @param path the path to send the message to
     * @param message the message
     * @param sendCallback the callback for when the send has completed
     */
    void sendToFilter(
        String sessionFilter,
        String path,
        V message,
        SendToFilterCallback sendCallback)
        throws TransformationException;
}
