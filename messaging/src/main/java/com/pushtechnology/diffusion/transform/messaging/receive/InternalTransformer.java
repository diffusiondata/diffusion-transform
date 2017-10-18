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

import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A transformer. Converts values of one type into values of a different
 * type. It can fail by throwing a {@link TransformationException}.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
/*package*/ interface InternalTransformer<S, T> {
    /**
     * Transform the value.
     *
     * @param value the value to transform
     * @return the transformed value
     * @throws TransformationException if the transformation cannot be applied
     */
    T transform(S value) throws TransformationException;

    /**
     * Chain a transformer.
     *
     * @param newTransformer the next transfomer transformer
     * @param <U> the target value type
     * @return the composed transformer
     */
    default <U> InternalTransformer<S, U> chain(InternalTransformer<T, U> newTransformer) {
        return value -> {
            if (value == null) {
                return null;
            }
            final T transientValue = this.transform(value);
            return newTransformer.transform(transientValue);
        };
    }

    /**
     * Chain an unsafe transformer.
     *
     * @param newTransformer the next transfomer transformer
     * @param <U> the target value type
     * @return the composed transformer
     */
    default <U> InternalTransformer<S, U> chainUnsafe(UnsafeTransformer<T, U> newTransformer) {
        return value -> {
            if (value == null) {
                return null;
            }
            final T transientValue = this.transform(value);
            return toTransformer(newTransformer).transform(transientValue);
        };
    }

    /**
     * Identity transformer.
     *
     * @param <T> the type of value
     * @return a transformer that transforms values to themselves.
     */
    /*package*/ static <T> InternalTransformer<T, T> identity() {
        return value -> value;
    }

    /**
     * Create an {@link InternalTransformer} from a {@link UnsafeTransformer}.
     * @param transformer the uncaught transformer
     * @param <S> the source value type
     * @param <T> the target value type
     * @return the transformer
     */
    /*package*/ static <S, T> InternalTransformer<S, T> toTransformer(
        final UnsafeTransformer<S, T> transformer) {
        return value -> {
            if (value == null) {
                return null;
            }

            try {
                return transformer.transform(value);
            }
            catch (TransformationException e) {
                throw e;
            }
            // CHECKSTYLE.OFF: IllegalCatch // Bulkhead
            catch (Exception e) {
                throw new TransformationException(e);
            }
            // CHECKSTYLE.ON: IllegalCatch // Bulkhead
        };
    }
}
