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

import static com.pushtechnology.diffusion.client.Diffusion.content;
import static com.pushtechnology.diffusion.client.Diffusion.dataTypes;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toByteArray;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Unit tests for Bytes to byte array transformer.
 *
 * @author Push Technology Limited
 */
public final class BytesToByteArrayTransformerTest {
    @Test
    public void testContent() {
        final byte[] bytes = new byte[] { 0x0, 0x1, 0x2, 0x5 };
        final Content content = content().newContent(bytes);

        final byte[] transformedBytes = toByteArray().transform(content);

        assertArrayEquals(bytes, transformedBytes);
    }

    @Test
    public void testBinary() {
        final byte[] bytes = new byte[] { 0x0, 0x1, 0x2, 0x5 };
        final Binary binary = dataTypes().binary().readValue(bytes);

        final byte[] transformedBytes = toByteArray().transform(binary);

        assertArrayEquals(bytes, transformedBytes);
    }

    @Test
    public void testJSON() {
        final byte[] bytes = new byte[] { 0x0, 0x1, 0x2, 0x5 };
        // Not valid CBOR but not checked
        final JSON json = dataTypes().json().readValue(bytes);

        final byte[] transformedBytes = toByteArray().transform(json);

        assertArrayEquals(bytes, transformedBytes);
    }
}
