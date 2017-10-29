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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.bigIntegerToBinary;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.binaryToBigInteger;
import static java.math.BigInteger.ONE;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.function.Function;

import com.pushtechnology.diffusion.datatype.binary.Binary;

import org.junit.Test;

/**
 * Transformation test strings.
 *
 * @author Push Technology Limited
 */
public final class BigIntegerTransformersTest {
    @Test
    public void testRoundTripConversion() throws Exception {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MAX_VALUE).add(ONE);

        final Function<BigInteger, Binary> toBinaryTransformer = bigIntegerToBinary();
        final UnsafeTransformer<Binary, BigInteger> toBigIntegerTransformer = binaryToBigInteger();

        final Binary binary = toBinaryTransformer.apply(bigInteger);
        final BigInteger newBigInteger = toBigIntegerTransformer.transform(binary);

        assertEquals(bigInteger, newBigInteger);
    }
}
