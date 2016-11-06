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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import com.pushtechnology.diffusion.datatype.binary.Binary;

/**
 * Abstract {@link Transformer} to help deserialise values from {@link Binary}.
 *
 * @param <T> the type to transform to
 * @author Push Technology Limited
 */
public abstract class FromBinaryTransformer<T> implements Transformer<Binary, T> {
    @Override
    public final T transform(Binary value) throws TransformationException {
        if (value == null) {
            return null;
        }

        final InputStream inputStream = value.asInputStream();
        final DataInputStream dataInputStream = new DataInputStream(inputStream);

        try {
            return deserialiseValue(dataInputStream);
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
     * Deserialise the value.
     *
     * @param dataInput input to read from
     * @throws TransformationException if the value cannot be deserialised
     * @throws java.io.IOException if the operations on the input failed
     * @throws Exception if the deserialisation failed unexpectedly
     */
    protected abstract T deserialiseValue(DataInput dataInput) throws Exception;
}
