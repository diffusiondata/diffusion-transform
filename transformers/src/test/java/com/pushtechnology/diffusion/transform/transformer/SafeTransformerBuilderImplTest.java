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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link SafeTransformerBuilderImpl}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class SafeTransformerBuilderImplTest {
    @Mock
    private SafeTransformer<String, String> safeTransformer0;
    @Mock
    private SafeTransformer<String, Integer> safeTransformer1;
    @Mock
    private Transformer<Integer, String> transformer;
    @Mock
    private UnsafeTransformer<Integer, String> unsafeTransformer;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(safeTransformer0.transform("hello")).thenReturn("morning");
        when(safeTransformer1.transform("morning")).thenReturn(42);
        when(transformer.transform(42)).thenReturn("goodbye");
        when(unsafeTransformer.transform(42)).thenReturn("goodbye");
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(transformer, safeTransformer0, safeTransformer1, unsafeTransformer);
    }

    @Test
    public void chainTransformers() throws Exception {
        final TransformerBuilder<String, String> transformerBuilder = new SafeTransformerBuilderImpl<>(safeTransformer0)
            .transform(safeTransformer1)
            .transform(transformer);

        final Transformer<String, String> builtTransformer = transformerBuilder.build();
        final String result = builtTransformer.transform("hello");

        assertEquals("goodbye", result);

        verify(safeTransformer0).transform("hello");
        verify(safeTransformer1).transform("morning");
        verify(transformer).transform(42);
    }

    @Test
    public void chainSafeTransformers() {
        final SafeTransformerBuilder<String, Integer> transformerBuilder =
            new SafeTransformerBuilderImpl<>(safeTransformer0)
                .transform(safeTransformer1);

        final SafeTransformer<String, Integer> transformer = transformerBuilder.build();
        final Integer result = transformer.transform("hello");

        assertEquals((int) 42,(int) result);

        verify(safeTransformer0).transform("hello");
        verify(safeTransformer1).transform("morning");
    }

    @Test
    public void chainUnsafeTransformers() throws Exception {
        final TransformerBuilder<String, String> transformerBuilder = new SafeTransformerBuilderImpl<>(safeTransformer0)
            .transform(safeTransformer1)
            .transformWith(unsafeTransformer);

        final Transformer<String, String> builtTransformer = transformerBuilder.build();
        final String result = builtTransformer.transform("hello");

        assertEquals("goodbye", result);

        verify(safeTransformer0).transform("hello");
        verify(safeTransformer1).transform("morning");
        verify(unsafeTransformer).transform(42);
    }
}
