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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.identity;

/**
 * Factory for creating instances of {@link UnboundRequestSenderBuilder}s.
 *
 * @author Push Technology Limited
 */
public final class RequestSenderBuilders {
    private RequestSenderBuilders() {
    }

    /**
     * Create a {@link UnboundRequestSenderBuilder}.
     *
     * @return the request sender builder
     */
    public static <U, V> UnboundRequestSenderBuilder<V, U, V> requestSenderBuilder(
            Class<U> requestType,
            Class<V> responseType) {
        return new UnboundRequestSenderBuilderImpl<>(requestType, responseType, identity(), identity());
    }
}
