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
 * Implementation of {@link SafeTransformerBuilder}.
 *
 * @param <S> The type of source value accepted by the transformers this builds
 * @param <T> The type of target value returned by the transformers this builds
 * @author Push Technology Limited
 */
/*package*/ final class SafeTransformerBuilderImpl<S, T> implements SafeTransformerBuilder<S, T> {
    private final Function<S, T> transformer;

    /*package*/ SafeTransformerBuilderImpl(Function<S, T> transformer) {
        this.transformer = transformer;
    }

    @Override
    public <R> TransformerBuilder<S, R> unsafeTransform(UnsafeTransformer<T, R> newTransformer) {
        return new TransformerBuilderImpl<>(value -> newTransformer.transform(transformer.apply(value)));
    }

    @Override
    public <R> SafeTransformerBuilder<S, R> transform(Function<T, R> newTransformer) {
        return new SafeTransformerBuilderImpl<>(value -> newTransformer.apply(transformer.apply(value)));
    }

    @Override
    public Function<S, T> buildSafe() {
        return transformer;
    }

    @Override
    public UnsafeTransformer<S, T> buildUnsafe() {
        return transformer::apply;
    }
}
