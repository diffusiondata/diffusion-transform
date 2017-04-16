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

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * Factory for creating instances of message receivers.
 *
 * @author Matt Champion 12/04/2017
 */
public final class MessageReceiverBuilders {
    private MessageReceiverBuilders() {
    }

    /**
     * Create a {@link UnboundSafeMessageReceiverBuilder} from a {@link Content} source.
     *
     * @return the message stream builder
     */
    public static UnboundSafeMessageReceiverBuilder<Content> newMessageReceiverBuilder() {
        return new UnboundSafeMessageReceiverBuilderImpl<>(Transformers.<Content>identity());
    }
}
