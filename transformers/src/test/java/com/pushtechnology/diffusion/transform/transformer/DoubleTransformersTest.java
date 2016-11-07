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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.binaryToDouble;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.doubleToBinary;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.pushtechnology.diffusion.datatype.binary.Binary;

/**
 * Transformation test for doubles.
 *
 * @author Push Technology Limited
 */
public final class DoubleTransformersTest {
    @Test
    public void testRoundTripConversion() throws TransformationException {
        final Transformer<Double, Binary> toBinaryTransformer = doubleToBinary();
        final Transformer<Binary, Double> toDoubleTransformer = binaryToDouble();

        final Binary binary = toBinaryTransformer.transform(0.512);
        final Double newDouble = toDoubleTransformer.transform(binary);

        assertEquals(0.512, newDouble, 0.1);
    }
}
