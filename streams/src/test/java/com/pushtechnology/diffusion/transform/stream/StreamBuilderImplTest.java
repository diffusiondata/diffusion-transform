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

package com.pushtechnology.diffusion.transform.stream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.topics.TopicSelector;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.Transformers;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link StreamBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class StreamBuilderImplTest {
    @Mock
    private Topics topics;
    @Mock
    private TopicSelector selector;
    @Mock
    private TransformedStream<String, String> stream;
    @Mock
    private TransformedStream<JSON, JSON> jsonStream;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void transform() {
        final StreamBuilder<String, String, TransformedStream<String, String>> streamBuilder =
            new StreamBuilderImpl<>(String.class, Transformers.<String>identity().asUnsafeTransformer());

        final StreamBuilder<String, String, TransformedStream<String, String>> transformedStreamBuilder =
            streamBuilder.transform(Transformers.<String>identity());

        assertTrue(transformedStreamBuilder instanceof StreamBuilderImpl);
    }

    @Test
    public void transformWith() {
        final StreamBuilder<String, String, TransformedStream<String, String>> streamBuilder =
            new StreamBuilderImpl<>(String.class, Transformers.<String>identity().asUnsafeTransformer());

        final StreamBuilder<String, String, TransformedStream<String, String>> transformedStreamBuilder =
            streamBuilder.transformWith(new UnsafeTransformer<String, String>() {
                @Override
                public String transform(String value) throws Exception {
                    return value;
                }
            });

        assertTrue(transformedStreamBuilder instanceof StreamBuilderImpl);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createPath() {
        final StreamBuilder<String, String, TransformedStream<String, String>> streamBuilder =
            new StreamBuilderImpl<>(String.class, Transformers.<String>identity().asUnsafeTransformer());
        streamBuilder.register(topics, "path", stream);

        verify(topics).addStream(eq("path"), eq(String.class), isA(StreamAdapter.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createSelector() {
        final StreamBuilder<String, String, TransformedStream<String, String>> streamBuilder =
            new StreamBuilderImpl<>(String.class, Transformers.<String>identity().asUnsafeTransformer());
        streamBuilder.register(topics, selector, stream);

        verify(topics).addStream(eq(selector), eq(String.class), isA(StreamAdapter.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createFallback() {
        final StreamBuilder<JSON, JSON, TransformedStream<JSON, JSON>> streamBuilder =
            new StreamBuilderImpl<>(JSON.class, Transformers.<JSON>identity().asUnsafeTransformer());
        streamBuilder.createFallback(topics, jsonStream);

        verify(topics).addFallbackStream(eq(JSON.class), isA(Topics.ValueStream.class));
    }
}
