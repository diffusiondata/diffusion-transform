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
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Unit tests for {@link BoundSafeMessageReceiverBuilderImpl}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class BoundSafeMessageReceiverBuilderImplTest {
    @Mock
    private Messaging messaging;
    @Mock
    private MessagingControl messagingControl;
    @Mock
    private Session session;
    @Mock
    private SafeTransformer<Content, String> contentTransformer;
    @Mock
    private Transformer<String, String> stringTransformer;
    @Mock
    private UnsafeTransformer<String, String> unsafeTransformer;
    @Mock
    private SafeTransformer<String, String> safeStringTransformer;
    @Mock
    private SafeMessageStream<String> messageStream;
    @Mock
    private SafeMessageHandler<String> messageHandler;

    @Before
    public void setUp() {
        initMocks(this);

        when(session.feature(Messaging.class)).thenReturn(messaging);
        when(session.feature(MessagingControl.class)).thenReturn(messagingControl);
    }

    @Test
    public void transformSafe() {
        final BoundSafeMessageReceiverBuilder<String> builder =
            new BoundSafeMessageReceiverBuilderImpl<>(contentTransformer, session);
        final BoundSafeMessageReceiverBuilder<String> newBuilder = builder.transform(safeStringTransformer);

        assertNotSame(builder, newBuilder);
    }

    @Test
    public void transform() {
        final BoundSafeMessageReceiverBuilder<String> builder =
            new BoundSafeMessageReceiverBuilderImpl<>(contentTransformer, session);
        final BoundTransformedMessageReceiverBuilder<String> newBuilder = builder.transform(stringTransformer);

        assertNotSame(builder, newBuilder);
    }

    @Test
    public void transformWith() {
        final BoundSafeMessageReceiverBuilder<String> builder =
            new BoundSafeMessageReceiverBuilderImpl<>(contentTransformer, session);
        final BoundTransformedMessageReceiverBuilder<String> newBuilder = builder.transformWith(unsafeTransformer);

        assertNotSame(builder, newBuilder);
    }

    @Test
    public void register() {
        final BoundSafeMessageReceiverBuilder<String> builder =
            new BoundSafeMessageReceiverBuilderImpl<>(contentTransformer, session);
        builder.register(messageStream);

        verify(session).feature(Messaging.class);
        verify(messaging).addFallbackMessageStream(isA(Messaging.MessageStream.class));
    }

    @Test
    public void registerWithSelector() {
        final BoundSafeMessageReceiverBuilder<String> builder =
            new BoundSafeMessageReceiverBuilderImpl<>(contentTransformer, session);
        builder.register("selector", messageStream);

        verify(session).feature(Messaging.class);
        verify(messaging).addMessageStream(eq("selector"), isA(Messaging.MessageStream.class));
    }

    @Test
    public void registerHandler() {
        final BoundSafeMessageReceiverBuilder<String> builder =
            new BoundSafeMessageReceiverBuilderImpl<>(contentTransformer, session);
        builder.register("selector", messageHandler);

        verify(session).feature(MessagingControl.class);
        verify(messagingControl).addMessageHandler(eq("selector"), isA(MessagingControl.MessageHandler.class));
    }
}
