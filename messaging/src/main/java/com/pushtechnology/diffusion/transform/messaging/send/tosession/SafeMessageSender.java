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

package com.pushtechnology.diffusion.transform.messaging.send.tosession;

import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendCallback;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendToFilterCallback;
import com.pushtechnology.diffusion.client.session.SessionId;

/**
 * A sender of values as messages. Safely transforms them before sending.
 *
 * @param <V> the type of values
 * @author Push Technology Limited
 */
public interface SafeMessageSender<V> extends MessageSender<V> {

    @Override
    void send(SessionId sessionId, String path, V message, SendCallback sendCallback);

    @Override
    void sendToFilter(String sessionFilter, String path, V message, SendToFilterCallback sendCallback);
}
