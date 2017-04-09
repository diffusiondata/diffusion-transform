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

import java.util.Map;

import com.pushtechnology.diffusion.client.session.SessionId;

/**
 * A handler of values received as messages from sessions.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
public interface MessageHandler<V> {

    /**
     * Notified when the registration is complete.
     */
    void onRegistered(String path);

    /**
     * Notified when a message is received.
     *
     * @param path the path to which the message was sent
     * @param message the message
     * @param sessionId the session that sent the message
     * @param sessionProperties the session properties of the session that sent the message
     */
    void onMessageReceived(String path, V message, SessionId sessionId, Map<String, String> sessionProperties);

    /**
     * Called if the handler is closed.
     *
     * @param path the branch of the topic tree for which the handler was
     *        registered
     */
    void onClose(String path);
}
