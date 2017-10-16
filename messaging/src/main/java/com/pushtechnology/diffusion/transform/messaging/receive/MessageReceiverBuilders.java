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

import static com.pushtechnology.diffusion.client.Diffusion.dataTypes;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.binary.BinaryDataType;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * Factory for creating instances of message receivers.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class MessageReceiverBuilders {

    private static final BinaryDataType BINARY_DATA_TYPE = dataTypes().binary();
    private static final SafeTransformer<Content, Binary> CONTENT_TO_BINARY = chain(
        Transformers.<Content>toByteArray(),
        new SafeTransformer<byte[], Binary>() {
            @Override
            public Binary transform(byte[] bytes) {
                return BINARY_DATA_TYPE.readValue(bytes);
            }
        });
    private static final JSONDataType JSON_DATA_TYPE = dataTypes().json();
    private static final SafeTransformer<Content, JSON> CONTENT_TO_JSON = chain(
        Transformers.<Content>toByteArray(),
        new SafeTransformer<byte[], JSON>() {
            @Override
            public JSON transform(byte[] bytes) {
                return JSON_DATA_TYPE.readValue(bytes);
            }
        });

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

    /**
     * Create a {@link UnboundSafeMessageReceiverBuilder} from a {@link JSON} source.
     *
     * @return the message stream builder
     */
    public static UnboundSafeMessageReceiverBuilder<JSON> newJSONMessageReceiverBuilder() {
        return new UnboundSafeMessageReceiverBuilderImpl<>(CONTENT_TO_JSON);
    }

    /**
     * Create a {@link UnboundSafeMessageReceiverBuilder} from a {@link Binary} source.
     *
     * @return the message stream builder
     */
    public static UnboundSafeMessageReceiverBuilder<Binary> newBinaryMessageReceiverBuilder() {
        return new UnboundSafeMessageReceiverBuilderImpl<>(CONTENT_TO_BINARY);
    }
}
