/*******************************************************************************
 * Copyright (C) 2017 Push Technology Ltd.
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.RegisteredHandler;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.client.types.ReceiveContext;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Unit tests for {@link TransformedMessageHandlerAdapter}.
 *
 * @author Push Technology Limited
 */
public final class TransformedMessageHandlerAdapterTest {
    private final Content content = Diffusion.content().newContent("value");
    @Mock
    private TransformedMessageHandler<String> messageHandler;
    @Mock
    private Transformer<Content, String> transformer;
    @Mock
    private ReceiveContext context;
    @Mock
    private SessionId sessionId;
    @Mock
    private RegisteredHandler registeredHandler;

    private Map<String, String> sessionProperties;

    @Before
    public void setUp() throws TransformationException {
        initMocks(this);

        sessionProperties = new HashMap<>();

        when(context.getSessionProperties()).thenReturn(sessionProperties);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(messageHandler, transformer, context, sessionId, registeredHandler);
    }

    @Test
    public void onMessageReceived() throws TransformationException {
        when(transformer.transform(content)).thenReturn("value");
        final TransformedMessageHandlerAdapter<String> adapter =
            new TransformedMessageHandlerAdapter<>(transformer, messageHandler, new HandlerHandle());
        adapter.onMessage(sessionId, "path", content, context);
        verify(messageHandler).onMessageReceived("path", "value", sessionId, sessionProperties);
        verify(transformer).transform(content);
        verify(context).getSessionProperties();
    }

    @Test
    public void onUntransformableMessageReceived() throws TransformationException {
        final TransformationException exception = new TransformationException("Intentionally thrown by test");
        doThrow(exception).when(transformer).transform(content);
        final TransformedMessageHandlerAdapter adapter =
            new TransformedMessageHandlerAdapter<>(transformer, messageHandler, new HandlerHandle());
        adapter.onMessage(sessionId, "path", content, context);
        verify(messageHandler).onTransformationException("path", content, sessionId, sessionProperties, exception);
        verify(transformer).transform(content);
        verify(context).getSessionProperties();
    }

    @Test
    public void onClose() {
        final TransformedMessageHandlerAdapter adapter =
            new TransformedMessageHandlerAdapter<>(transformer, messageHandler, new HandlerHandle());
        adapter.onClose("path");
        verify(messageHandler).onClose("path");
    }

    @Test
    public void onActive() {
        final HandlerHandle handle = new HandlerHandle();
        final TransformedMessageHandlerAdapter adapter =
            new TransformedMessageHandlerAdapter<>(transformer, messageHandler, handle);
        adapter.onActive("path", registeredHandler);
        verify(messageHandler).onRegistered("path");

        handle.close();
        verify(registeredHandler).close();
    }
}