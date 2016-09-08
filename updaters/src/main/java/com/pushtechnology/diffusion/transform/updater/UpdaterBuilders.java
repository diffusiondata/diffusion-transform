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

package com.pushtechnology.diffusion.transform.updater;

import static com.pushtechnology.diffusion.transform.transformer.Transformers.identity;

import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * Utilities for values updaters.
 *
 * @author Push Technology Limited
 */
public final class UpdaterBuilders {
    private UpdaterBuilders() {
    }

    /**
     * Create a builder for updaters.
     * @param valueType The value type
     * @param <V> Value type
     * @return The builder
     */
    public static <V> UnboundSafeUpdaterBuilder<V, V> updaterBuilder(Class<V> valueType) {
        return new UnboundSafeUpdaterBuilderImpl<>(
            valueType,
            Transformers.<V>identity());
    }

    /**
     * Create a builder for {@link JSON} updaters.
     * @return The builder
     */
    public static UnboundSafeUpdaterBuilder<JSON, JSON> jsonUpdaterBuilder() {
        return new UnboundSafeUpdaterBuilderImpl<>(JSON.class, identity(JSON.class));
    }

    /**
     * Create a builder for {@link Binary} updaters.
     * @return The builder
     */
    public static UnboundSafeUpdaterBuilder<Binary, Binary> binaryUpdaterBuilder() {
        return new UnboundSafeUpdaterBuilderImpl<>(Binary.class, identity(Binary.class));
    }
}
