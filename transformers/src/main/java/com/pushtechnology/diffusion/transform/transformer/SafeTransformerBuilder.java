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
 * Builder for {@link SafeTransformer}s.
 *
 * @param <S> The type of source value accepted by the transformers this builds
 * @param <T> The type of target value returned by the transformers this builds
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public interface SafeTransformerBuilder<S, T> extends TransformerBuilder<S, T> {
    /**
     * Transform the transformer that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new transformer builder
     */
    <R> SafeTransformerBuilder<S, R> transform(SafeTransformer<T, R> newTransformer);

    @Override
    <R> SafeTransformerBuilder<S, R> transform(Function<T, R> newTransformer);

    /**
     * @return a new transformer
     */
    SafeTransformer<S, T> build();
}
