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

import com.pushtechnology.diffusion.client.features.Messaging;

/**
 * Implementation of {@link MessageReceiverHandle} for {@link MessageStream}s.
 *
 * @author Push Technology Limited
 * @deprecated since 2.0.0 in favour of request receivers
 */
@Deprecated
/*package*/ final class StreamHandle implements MessageReceiverHandle {
    private final Messaging messaging;
    private final Messaging.MessageStream stream;

    StreamHandle(Messaging messaging, Messaging.MessageStream stream) {
        this.messaging = messaging;
        this.stream = stream;
    }

    @Override
    public void close() {
        messaging.removeMessageStream(stream);
    }
}
