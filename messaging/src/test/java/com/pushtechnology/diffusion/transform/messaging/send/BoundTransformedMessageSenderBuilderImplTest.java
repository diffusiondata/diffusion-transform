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

package com.pushtechnology.diffusion.transform.messaging.send;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Unit tests for {@link BoundTransformedMessageSenderBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class BoundTransformedMessageSenderBuilderImplTest {
    @Mock
    private SafeTransformer<String, JSON> transformer;
    @Mock
    private Transformer<String, String> stringTransformer;
    @Mock
    private UnsafeTransformer<String, String> unsafeTransformer;
    @Mock
    private Session session;
    @Mock
    private MessagingControl messagingControl;
    @Mock
    private Messaging messaging;
    @Mock
    private JSON json;
    @Mock
    private MessagingControl.SendCallback sendCallback;
    @Mock
    private Messaging.SendCallback sendToHandlerCallback;
    @Mock
    private SessionId sessionId;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(transformer.transform("value")).thenReturn(json);
        when(stringTransformer.transform("value")).thenReturn("value");
        when(unsafeTransformer.transform("value")).thenReturn("value");

        when(session.feature(MessagingControl.class)).thenReturn(messagingControl);
        when(session.feature(Messaging.class)).thenReturn(messaging);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(transformer, unsafeTransformer, session, messagingControl, json, sendCallback);
    }

    @Test
    public void transformAndSendToSession() throws Exception {
        final BoundTransformedMessageSenderBuilder<String> builder =
            new BoundTransformedMessageSenderBuilderImpl<>(session, transformer).transform(stringTransformer);

        final MessageToSessionSender<String> sender = builder.buildToSessionSender();
        sender.send(sessionId, "path", "value", sendCallback);

        verify(session).feature(MessagingControl.class);
        verify(transformer).transform("value");
        verify(stringTransformer).transform("value");
        verify(messagingControl).send(sessionId, "path", json, sendCallback);
    }

    @Test
    public void transformWithAndSendToSession() throws Exception {
        final BoundTransformedMessageSenderBuilder<String> builder =
            new BoundTransformedMessageSenderBuilderImpl<>(session, transformer).transformWith(unsafeTransformer);

        final MessageToSessionSender<String> sender = builder.buildToSessionSender();
        sender.send(sessionId, "path", "value", sendCallback);

        verify(session).feature(MessagingControl.class);
        verify(transformer).transform("value");
        verify(unsafeTransformer).transform("value");
        verify(messagingControl).send(sessionId, "path", json, sendCallback);
    }

    @Test
    public void transformAndSendToHandler() throws Exception {
        final BoundTransformedMessageSenderBuilder<String> builder =
            new BoundTransformedMessageSenderBuilderImpl<>(session, transformer).transform(stringTransformer);

        final MessageToHandlerSender<String> sender = builder.buildToHandlerSender();
        sender.send("path", "value", sendToHandlerCallback);

        verify(session).feature(Messaging.class);
        verify(transformer).transform("value");
        verify(stringTransformer).transform("value");
        verify(messaging).send("path", json, sendToHandlerCallback);
    }

    @Test
    public void transformWithAndSendToHandler() throws Exception {
        final BoundTransformedMessageSenderBuilder<String> builder =
            new BoundTransformedMessageSenderBuilderImpl<>(session, transformer).transformWith(unsafeTransformer);

        final MessageToHandlerSender<String> sender = builder.buildToHandlerSender();
        sender.send("path", "value", sendToHandlerCallback);

        verify(session).feature(Messaging.class);
        verify(transformer).transform("value");
        verify(unsafeTransformer).transform("value");
        verify(messaging).send("path", json, sendToHandlerCallback);
    }
}
