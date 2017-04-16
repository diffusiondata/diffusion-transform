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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.types.ReceiveContext;
import com.pushtechnology.diffusion.transform.messaging.receive.TransformedMessageStream;
import com.pushtechnology.diffusion.transform.messaging.receive.TransformedMessageStreamAdapter;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Unit tests for {@link TransformedMessageStreamAdapter}.
 *
 * @author Push Technology Limited
 */
public final class TransformedMessageStreamAdapterTest {
    private final Content content = Diffusion.content().newContent("value");
    @Mock
    private TransformedMessageStream<String> messageStream;
    @Mock
    private Transformer<Content, String> transformer;
    @Mock
    private ReceiveContext context;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(messageStream, transformer, context);
    }

    @Test
    public void onMessageReceived() throws TransformationException {
        when(transformer.transform(content)).thenReturn("value");
        final TransformedMessageStreamAdapter adapter =
            new TransformedMessageStreamAdapter<>(transformer, messageStream);
        adapter.onMessageReceived("path", content, context);
        verify(messageStream).onMessageReceived("path", "value");
        verify(transformer).transform(content);
    }

    @Test
    public void onUntransformableMessageReceived() throws TransformationException {
        final TransformationException exception = new TransformationException("Intentionally thrown by test");
        doThrow(exception).when(transformer).transform(content);
        final TransformedMessageStreamAdapter adapter =
            new TransformedMessageStreamAdapter<>(transformer, messageStream);
        adapter.onMessageReceived("path", content, context);
        verify(messageStream).onTransformationException("path", content, exception);
        verify(transformer).transform(content);
    }

    @Test
    public void onClose() {
        final TransformedMessageStreamAdapter adapter =
            new TransformedMessageStreamAdapter<>(transformer, messageStream);
        adapter.onClose();
        verify(messageStream).onClose();
    }

    @Test
    public void onError() {
        final TransformedMessageStreamAdapter adapter =
            new TransformedMessageStreamAdapter<>(transformer, messageStream);
        adapter.onError(ErrorReason.ACCESS_DENIED);
        verify(messageStream).onError(ErrorReason.ACCESS_DENIED);
    }
}
