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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.binary.BinaryDataType;

/**
 * Abstract transformer to help serialise values to {@link Binary}.
 *
 * @param <S> the type to transform from
 * @author Push Technology Limited
 */
public abstract class ToBinaryTransformer<S> implements UnsafeTransformer<S, Binary> {
    private static final BinaryDataType BINARY_DATA_TYPE = Diffusion.dataTypes().binary();
    private final int initialSize;

    /**
     * Constructor.
     *
     * @param initialSize the initial bytes allocated
     */
    protected ToBinaryTransformer(int initialSize) {
        this.initialSize = initialSize;
    }

    @Override
    public final Binary transform(S value) throws Exception {
        if (value == null) {
            return null;
        }

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(initialSize);
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            serialiseValue(dataOutputStream, value);
        }
        catch (TransformationException e) {
            throw e;
        }
        // CHECKSTYLE.OFF: IllegalCatch // Bulkhead
        catch (Exception e) {
            throw new TransformationException(e);
        }
        // CHECKSTYLE.ON: IllegalCatch // Bulkhead

        return BINARY_DATA_TYPE.readValue(byteArrayOutputStream.toByteArray());
    }

    /**
     * Serialise the value.
     * @param dataOutput output to write to
     * @param value value to serialise
     * @throws TransformationException if the value cannot be serialised
     * @throws java.io.IOException if the operations on the output failed
     * @throws Exception if the serialisation failed unexpectedly
     */
    protected abstract void serialiseValue(DataOutput dataOutput, S value) throws Exception;

    @Override
    public final String toString() {
        return getClass().getSimpleName();
    }
}
