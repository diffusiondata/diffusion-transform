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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.identity;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.topics.TopicSelector;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Unit tests for {@link SafeStreamBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class SafeStreamBuilderImplTest {
    @Mock
    private Topics topics;
    @Mock
    private TopicSelector selector;
    @Mock
    private Topics.ValueStream<String> stream;
    @Mock
    private Topics.ValueStream<JSON> jsonStream;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void safeTransform() {
        final SafeStreamBuilder<String, String> streamBuilder =
            new SafeStreamBuilderImpl<>(String.class, identity(String.class));

        final SafeStreamBuilder<String, String> transformedStreamBuilder =
            streamBuilder.transform(identity(String.class));

        assertTrue(transformedStreamBuilder instanceof SafeStreamBuilderImpl);
    }

    @Test
    public void transform() {
        final StreamBuilder<String, String, Topics.ValueStream<String>> streamBuilder =
            new SafeStreamBuilderImpl<>(String.class, identity(String.class));

        final StreamBuilder<String, String, TransformedStream<String, String>> transformedStreamBuilder =
            streamBuilder.transform(identity(String.class));

        assertTrue(transformedStreamBuilder instanceof StreamBuilderImpl);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createPath() {
        final StreamBuilder<String, String, Topics.ValueStream<String>> streamBuilder =
            new SafeStreamBuilderImpl<>(String.class, identity(String.class));
        streamBuilder.create(topics, "path", stream);

        verify(topics).addStream(eq("path"), eq(String.class), isA(SafeStreamAdapter.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createSelector() {
        final StreamBuilder<String, String, Topics.ValueStream<String>> streamBuilder =
            new SafeStreamBuilderImpl<>(String.class, identity(String.class));
        streamBuilder.create(topics, selector, stream);

        verify(topics).addStream(eq(selector), eq(String.class), isA(SafeStreamAdapter.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createFallback() {
        final StreamBuilder<JSON, JSON, Topics.ValueStream<JSON>> streamBuilder =
            new SafeStreamBuilderImpl<>(JSON.class, identity(JSON.class));
        streamBuilder.createFallback(topics, jsonStream);

        verify(topics).addFallbackStream(eq(JSON.class), isA(Topics.ValueStream.class));
    }
}
