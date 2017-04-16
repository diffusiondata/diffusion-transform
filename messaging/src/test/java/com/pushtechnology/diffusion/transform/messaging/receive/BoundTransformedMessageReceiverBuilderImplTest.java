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

package com.pushtechnology.diffusion.transform.messaging.receive;

import static org.junit.Assert.assertNotSame;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Unit tests for {@link BoundTransformedMessageReceiverBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class BoundTransformedMessageReceiverBuilderImplTest {

    @Mock
    private Messaging messaging;
    @Mock
    private Session session;
    @Mock
    private SafeTransformer<Content, String> contentTransformer;
    @Mock
    private Transformer<String, String> stringTransformer;
    @Mock
    private UnsafeTransformer<String, String> unsafeTransformer;
    @Mock
    private TransformedMessageStream<String> messageStream;

    @Before
    public void setUp() {
        initMocks(this);

        when(session.feature(Messaging.class)).thenReturn(messaging);
    }

    @Test
    public void transform() {
        final BoundTransformedMessageReceiverBuilder<String> builder =
            new BoundTransformedMessageReceiverBuilderImpl<>(contentTransformer, session);
        final BoundTransformedMessageReceiverBuilder<String> newBuilder = builder.transform(stringTransformer);

        assertNotSame(builder, newBuilder);
    }

    @Test
    public void transformWith() {
        final BoundTransformedMessageReceiverBuilder<String> builder =
            new BoundTransformedMessageReceiverBuilderImpl<>(contentTransformer, session);
        final BoundTransformedMessageReceiverBuilder<String> newBuilder = builder.transformWith(unsafeTransformer);

        assertNotSame(builder, newBuilder);
    }

    @Test
    public void register() {
        final BoundTransformedMessageReceiverBuilder<String> builder =
            new BoundTransformedMessageReceiverBuilderImpl<>(contentTransformer, session);
        builder.register(messageStream);

        verify(session).feature(Messaging.class);
        verify(messaging).addFallbackMessageStream(isA(Messaging.MessageStream.class));
    }
}
