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

package com.pushtechnology.diffusion.transform.transformer;

import java.util.function.Function;

/**
 * A transformer. Converts values of one type into values of a different
 * type. It can fail by throwing any exception.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
public interface UnsafeTransformer<S, T> {
    /**
     * Transform the value.
     *
     * @param value the value to transform
     * @return the transformed value
     * @throws Exception if the transformation cannot be applied
     */
    T transform(S value) throws Exception;

    /**
     * Chain a function after this transformer.
     *
     * @param function the function to apply
     * @param <U> the return type of the chained transformer
     * @return a new transformer
     */
    default <U> UnsafeTransformer<S, U> chain(Function<T, U> function) {
        return value -> function.apply(this.transform(value));
    }

    /**
     * Chain a unsafe transformer after this transformer.
     *
     * @param transformer the unsafe transformer to apply
     * @param <U> the return type of the chained transformer
     * @return a new transformer
     */
    default <U> UnsafeTransformer<S, U> chainUnsafe(UnsafeTransformer<T, U> transformer) {
        return value -> transformer.transform(this.transform(value));
    }
}
