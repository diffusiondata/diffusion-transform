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
 * Unit tests for {@link TransformerBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class TransformerBuilderImplTest {
    @Mock
    private Transformer<String, String> transformer;
    @Mock
    private SafeTransformer<String, Integer> safeTransformer;
    @Mock
    private UnsafeTransformer<Integer, String> unsafeTransformer;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(transformer.transform("hello")).thenReturn("morning");
        when(safeTransformer.transform("morning")).thenReturn(42);
        when(unsafeTransformer.transform(42)).thenReturn("goodbye");
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(transformer, safeTransformer, unsafeTransformer);
    }

    @Test
    public void chainTransformers() throws Exception {
        final TransformerBuilder<String, String> transformerBuilder = new TransformerBuilderImpl<>(transformer)
            .transform(safeTransformer)
            .transform(unsafeTransformer);

        final Transformer<String, String> builtTransformer = transformerBuilder.build();
        final String result = builtTransformer.transform("hello");

        assertEquals("goodbye", result);

        verify(transformer).transform("hello");
        verify(safeTransformer).transform("morning");
        verify(unsafeTransformer).transform(42);
    }

}
