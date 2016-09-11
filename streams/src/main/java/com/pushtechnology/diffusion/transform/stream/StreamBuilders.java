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

package com.pushtechnology.diffusion.transform.stream;

import static com.pushtechnology.diffusion.transform.transformer.Transformers.identity;

import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Factory for creating instances of {@link SafeStreamBuilder}s and {@link StreamBuilder}s.
 *
 * @author Push Technology Limited
 */
public final class StreamBuilders {
    private StreamBuilders() {
    }

    /**
     * Create a {@link StreamBuilder} from a source.
     *
     * @param valueType the type value of the source values
     * @param <V> the type of the source values
     * @return The stream builder
     */
    public static <V> SafeStreamBuilder<V, V> newStreamBuilder(Class<V> valueType) {
        return new SafeStreamBuilderImpl<>(valueType, identity(valueType));
    }

    /**
     * Create a {@link StreamBuilder} from a {@link Binary} source.
     *
     * @return The stream builder
     */
    public static SafeStreamBuilder<Binary, Binary> newBinaryStreamBuilder() {
        return new SafeStreamBuilderImpl<>(Binary.class, identity(Binary.class));
    }

    /**
     * Create a {@link StreamBuilder} from a {@link JSON} source.
     *
     * @return The stream builder
     */
    public static SafeStreamBuilder<JSON, JSON> newJsonStreamBuilder() {
        return new SafeStreamBuilderImpl<>(JSON.class, identity(JSON.class));
    }
}
