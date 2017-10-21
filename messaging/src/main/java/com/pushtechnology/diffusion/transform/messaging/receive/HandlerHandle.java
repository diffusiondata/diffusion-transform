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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.pushtechnology.diffusion.client.features.RegisteredHandler;

/**
 * Implementation of {@link MessageReceiverHandle} for handlers.
 *
 * @author Push Technology Limited
 * @deprecated since 2.0.0 in favour of request receivers
 */
@Deprecated
/*package*/ final class HandlerHandle implements MessageReceiverHandle {
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicReference<RegisteredHandler> registeredHandler = new AtomicReference<>(null);

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            final RegisteredHandler handler = this.registeredHandler.get();
            if (handler != null) {
                handler.close();
            }
        }
    }

    /*package*/ void setHandle(RegisteredHandler handle) {
        registeredHandler.set(handle);
        if (closed.get()) {
            handle.close();
        }
    }
}
