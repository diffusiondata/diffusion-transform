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

import com.pushtechnology.diffusion.datatype.binary.Binary;

/**
 * Transformer from {@link Binary} to {@link BigInteger}.
 *
 * @author Push Technology Limited
 */
public final class BinaryToBigIntegerTransformer extends AbstractTransformer<Binary, BigInteger> {
    /**
     * Instance of {@link BinaryToBigIntegerTransformer}.
     */
    public static final UnsafeTransformer<Binary, BigInteger> INSTANCE = new BinaryToBigIntegerTransformer();

    private BinaryToBigIntegerTransformer() {
    }

    @Override
    protected BigInteger transformUnsafely(Binary value) throws NumberFormatException, ArithmeticException {
        return new BigInteger(value.toByteArray());
    }
}
