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

package com.pushtechnology.diffusion.transform.adder;

import static com.pushtechnology.diffusion.transform.transformer.Transformers.identity;

import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.recordv2.RecordV2;

/**
 * Factory for creating instances of {@link TopicAdderBuilder}s.
 *
 * @author Push Technology Limited
 * @deprecated since 2.0.0
 */
@Deprecated
public final class TopicAdderBuilders {
    private TopicAdderBuilders() {
    }

    /**
     * Create a builder for {@link JSON} topic adders.
     * @param valueType The value type
     * @param <V> The value type
     * @return The builder
     * @throws IllegalArgumentException If there is no known topic type for the value
     */
    public static <V extends Bytes> UnboundSafeTopicAdderBuilder<V, V> topicAdderBuilder(Class<V> valueType) {
        return new UnboundSafeTopicAdderBuilderImpl<>(topicTypeFromValueType(valueType), identity(valueType));
    }

    /**
     * Create a builder for {@link JSON} topic adders.
     * @return The builder
     */
    public static UnboundSafeTopicAdderBuilder<JSON, JSON> jsonTopicAdderBuilder() {
        return new UnboundSafeTopicAdderBuilderImpl<>(TopicType.JSON, identity(JSON.class));
    }

    /**
     * Create a builder for {@link Binary} topic adders.
     * @return The builder
     */
    public static UnboundSafeTopicAdderBuilder<Binary, Binary> binaryTopicAdderBuilder() {
        return new UnboundSafeTopicAdderBuilderImpl<>(TopicType.BINARY, identity(Binary.class));
    }

    /**
     * Returns the topic type for a given value type.
     * @throws IllegalArgumentException If there is no known topic type for the value
     */
    /*package*/ static TopicType topicTypeFromValueType(Class<?> valueType) {
        if (JSON.class.isAssignableFrom(valueType)) {
            return TopicType.JSON;
        }
        else if (Binary.class.isAssignableFrom(valueType)) {
            return TopicType.BINARY;
        }
        else if (String.class.isAssignableFrom(valueType)) {
            return TopicType.STRING;
        }
        else if (Double.class.isAssignableFrom(valueType)) {
            return TopicType.DOUBLE;
        }
        else if (Long.class.isAssignableFrom(valueType)) {
            return TopicType.INT64;
        }
        else if (RecordV2.class.isAssignableFrom(valueType)) {
            return TopicType.RECORD_V2;
        }
        else {
            throw new IllegalArgumentException("The topic type for " + valueType + " is unknown");
        }
    }
}
