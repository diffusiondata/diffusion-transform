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

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.nio.charset.Charset;

import org.junit.Test;

import com.pushtechnology.diffusion.datatype.binary.Binary;

/**
 * Transformation test strings.
 *
 * @author Push Technology Limited
 */
public final class StringTransformersTest {
    @Test
    public void testUtf8RoundTripConversion() throws TransformationException {
        assumeTrue(Charset.isSupported("UTF-8"));

        final Transformer<String, Binary> toBinaryTransformer = Transformers.stringToBinary(Charset.forName("UTF-8"));
        final Transformer<Binary, String> toStringTransformer = Transformers.binaryToString(Charset.forName("UTF-8"));

        final Binary binary = toBinaryTransformer.transform("string to binary \u2200");
        final String newString = toStringTransformer.transform(binary);

        assertEquals("string to binary \u2200", newString);
    }

    @Test
    public void testUtf16RoundTripConversion() throws TransformationException {
        assumeTrue(Charset.isSupported("UTF-16"));

        final Transformer<String, Binary> toBinaryTransformer = Transformers.stringToBinary(Charset.forName("UTF-16"));
        final Transformer<Binary, String> toStringTransformer = Transformers.binaryToString(Charset.forName("UTF-16"));

        final Binary binary = toBinaryTransformer.transform("string to binary \u2200");
        final String newString = toStringTransformer.transform(binary);

        assertEquals("string to binary \u2200", newString);
    }
}
