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
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Unit tests for {@link AbstractTransformer}.
 *
 * @author Push Technology Limited
 */
public final class AbstractTransformerTest {

    @Test
    public void transform() throws TransformationException {
        final IdentityTransformer transformer = new IdentityTransformer();
        final String transformedValue = transformer.transform("value");
        assertEquals("value", transformedValue);
    }

    @Test
    public void transformNull() throws TransformationException {
        final IdentityTransformer transformer = new IdentityTransformer();
        final String transformedValue = transformer.transform(null);
        assertNull(transformedValue);
    }

    @Test(expected = TransformationException.class)
    public void failure() throws TransformationException {
        final ThrowingTransformer transformer = new ThrowingTransformer();
        transformer.transform("value");
    }

    @Test(expected = TransformationException.class)
    public void transformationException() throws TransformationException {
        final TransformationExceptionThrowingTransformer transformer = new TransformationExceptionThrowingTransformer();
        transformer.transform("value");
    }

    private static final class IdentityTransformer extends AbstractTransformer<String, String> {
        @Override
        protected String transformUnsafely(String value) throws Exception {
            return value;
        }
    }

    private static final class ThrowingTransformer extends AbstractTransformer<String, String> {
        @Override
        protected String transformUnsafely(String value) throws Exception {
            throw new Exception("Intentionally thrown by test");
        }
    }

    private static final class TransformationExceptionThrowingTransformer extends AbstractTransformer<String, String> {
        @Override
        protected String transformUnsafely(String value) throws Exception {
            throw new TransformationException("Intentionally thrown by test");
        }
    }
}
