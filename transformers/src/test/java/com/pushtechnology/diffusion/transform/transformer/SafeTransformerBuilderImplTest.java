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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link SafeTransformerBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class SafeTransformerBuilderImplTest {
    @Mock
    private Function<String, String> safeTransformer0;
    @Mock
    private Function<String, Integer> safeTransformer1;
    @Mock
    private UnsafeTransformer<Integer, String> transformer;
    @Mock
    private UnsafeTransformer<Integer, String> unsafeTransformer;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(safeTransformer0.apply("hello")).thenReturn("morning");
        when(safeTransformer1.apply("morning")).thenReturn(42);
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
            .unsafeTransform(transformer);

        final UnsafeTransformer<String, String> builtTransformer = transformerBuilder.buildUnsafe();
        final String result = builtTransformer.transform("hello");

        assertEquals("goodbye", result);

        verify(safeTransformer0).apply("hello");
        verify(safeTransformer1).apply("morning");
        verify(transformer).transform(42);

        final UnsafeTransformer<String, String> unsafeBuiltTransformer = transformerBuilder.buildUnsafe();
        final String unsafeResult = unsafeBuiltTransformer.transform("hello");

        assertEquals("goodbye", unsafeResult);

        verify(safeTransformer0, times(2)).apply("hello");
        verify(safeTransformer1, times(2)).apply("morning");
        verify(transformer, times(2)).transform(42);
    }

    @Test
    public void chainSafeTransformers() throws Exception {
        final SafeTransformerBuilder<String, Integer> transformerBuilder =
            new SafeTransformerBuilderImpl<>(safeTransformer0)
                .transform(safeTransformer1)
                .transform(Function.identity());

        final Function<String, Integer> transformer = transformerBuilder.buildSafe();
        final Integer result = transformer.apply("hello");

        assertEquals(42, (int) result);

        verify(safeTransformer0).apply("hello");
        verify(safeTransformer1).apply("morning");

        final UnsafeTransformer<String, Integer> unsafeBuiltTransformer = transformerBuilder.buildUnsafe();
        final Integer unsafeResult = unsafeBuiltTransformer.transform("hello");

        assertEquals(42, (int) unsafeResult);

        verify(safeTransformer0, times(2)).apply("hello");
        verify(safeTransformer1, times(2)).apply("morning");
    }

    @Test
    public void chainUnsafeTransformers() throws Exception {
        final TransformerBuilder<String, String> transformerBuilder = new SafeTransformerBuilderImpl<>(safeTransformer0)
            .transform(safeTransformer1)
            .unsafeTransform(unsafeTransformer);

        final UnsafeTransformer<String, String> builtTransformer = transformerBuilder.buildUnsafe();
        final String result = builtTransformer.transform("hello");

        assertEquals("goodbye", result);

        verify(safeTransformer0).apply("hello");
        verify(safeTransformer1).apply("morning");
        verify(unsafeTransformer).transform(42);

        final UnsafeTransformer<String, String> unsafeBuiltTransformer = transformerBuilder.buildUnsafe();
        final String unsafeResult = unsafeBuiltTransformer.transform("hello");

        assertEquals("goodbye", unsafeResult);

        verify(safeTransformer0, times(2)).apply("hello");
        verify(safeTransformer1, times(2)).apply("morning");
        verify(unsafeTransformer, times(2)).transform(42);
    }
}
