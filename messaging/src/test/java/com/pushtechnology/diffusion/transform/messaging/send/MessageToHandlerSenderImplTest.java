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
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Unit tests for {@link MessageToHandlerSenderImpl}.
 *
 * @author Matt Champion 11/04/2017
 */
public final class MessageToHandlerSenderImplTest {
    @Mock
    private Transformer<String, JSON> transformer;
    @Mock
    private Messaging messaging;
    @Mock
    private JSON json;
    @Mock
    private Messaging.SendCallback sendCallback;

    @Before
    public void setUp() throws TransformationException {
        initMocks(this);

        when(transformer.transform("value")).thenReturn(json);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(transformer, messaging, json, sendCallback);
    }

    @Test
    public void send() throws Exception {
        final MessageToHandlerSender<String> sender = new MessageToHandlerSenderImpl<>(messaging, transformer);

        sender.send("path", "value", sendCallback);

        verify(transformer).transform("value");
        verify(messaging).send("path", json, sendCallback);
    }
}
