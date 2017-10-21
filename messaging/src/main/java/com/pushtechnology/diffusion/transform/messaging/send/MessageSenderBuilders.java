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

import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * Factory for creating instances of {@link SafeMessageSenderBuilder}s.
 *
 * @author Push Technology Limited
 * @deprecated since 2.0.0 in favour of request senders
 */
@Deprecated
public final class MessageSenderBuilders {
    private MessageSenderBuilders() {
    }

    /**
     * Create a {@link SafeMessageSenderBuilder} from a {@link Bytes} source.
     *
     * @return the message sender builder
     */
    public static UnboundSafeMessageSenderBuilder<Bytes> newMessageSenderBuilder() {
        return new UnboundSafeMessageSenderBuilderImpl<>(Transformers.<Bytes>identity());
    }

    /**
     * Create a {@link SafeMessageSenderBuilder} from a {@link JSON} source.
     *
     * @return the message sender builder
     */
    public static UnboundSafeMessageSenderBuilder<JSON> newJSONMessageSenderBuilder() {
        return new UnboundSafeMessageSenderBuilderImpl<>(Transformers.<JSON>identity());
    }

    /**
     * Create a {@link SafeMessageSenderBuilder} from a {@link Binary} source.
     *
     * @return the message sender builder
     */
    public static UnboundSafeMessageSenderBuilder<Binary> newBinaryMessageSenderBuilder() {
        return new UnboundSafeMessageSenderBuilderImpl<>(Transformers.<Binary>identity());
    }
}
