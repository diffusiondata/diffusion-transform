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
 * Abstract {@link Transformer} that catches and converts any exception to a
 * {@link TransformationException}.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
public abstract class AbstractTransformer<S, T> implements Transformer<S, T> {
    @Override
    public final T transform(S value) throws TransformationException {
        if (value == null) {
            return null;
        }

        try {
            return transformUnsafely(value);
        }
        catch (TransformationException e) {
            throw e;
        }
        // CHECKSTYLE.OFF: IllegalCatch // Bulkhead
        catch (Exception e) {
            throw new TransformationException(e);
        }
        // CHECKSTYLE.ON: IllegalCatch // Bulkhead
    }

    /**
     * Transform the value.
     *
     * @param value the value to transform
     * @return the transformed value
     * @throws Exception if the transformation cannot be applied
     */
    protected abstract T transformUnsafely(S value) throws Exception;
}
