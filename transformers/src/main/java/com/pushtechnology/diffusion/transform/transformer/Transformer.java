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

/**
 * A transformer. Converts values of one type into values of a different
 * type. It can fail by throwing a {@link TransformationException}.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 * @deprecated since 2.0.0 in favour of {@link UnsafeTransformer}
 */
@Deprecated
public interface Transformer<S, T> {
    /**
     * Transform the value.
     *
     * @param value the value to transform
     * @return the transformed value
     * @throws TransformationException if the transformation cannot be applied
     */
    T transform(S value) throws TransformationException;

    /**
     * Convert the {@link Transformer} to an {@link UnsafeTransformer}.
     *
     * @return the unsafe transformer
     */
    default UnsafeTransformer<S, T> asUnsafeTransformer() {
        return this::transform;
    }
}
