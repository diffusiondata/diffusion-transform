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

import java.math.BigInteger;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.binary.BinaryDataType;

/**
 * Transformer from {@link BigInteger} to {@link Binary}.
 *
 * @author Push Technology Limited
 */
/*package*/ final class BigIntegerToBinaryTransformer implements SafeTransformer<BigInteger, Binary> {
    /**
     * Instance of {@link BinaryToBigIntegerTransformer}.
     */
    public static final SafeTransformer<BigInteger, Binary> INSTANCE = new BigIntegerToBinaryTransformer();
    private static final BinaryDataType BINARY_DATA_TYPE = Diffusion.dataTypes().binary();

    private BigIntegerToBinaryTransformer() {
    }
    @Override
    public Binary transform(BigInteger value) {
        return BINARY_DATA_TYPE.readValue(value.toByteArray());
    }
}
