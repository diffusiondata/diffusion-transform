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

import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendCallback;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendToFilterCallback;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;

/**
 * Unit tests for {@link SafeMessageToSessionSenderImpl}.
 *
 * @author Matt Champion 11/04/2017
 */
public final class SafeMessageToSessionSenderImplTest {
    @Mock
    private SafeTransformer<String, JSON> transformer;
    @Mock
    private MessagingControl messagingControl;
    @Mock
    private JSON json;
    @Mock
    private SendCallback sendCallback;
    @Mock
    private SendToFilterCallback sendToFilterCallback;
    @Mock
    private SessionId sessionId;

    @Before
    public void setUp() {
        initMocks(this);

        when(transformer.transform("value")).thenReturn(json);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(transformer, messagingControl, json, sendCallback, sendToFilterCallback);
    }

    @Test
    public void send() {
        final SafeMessageToSessionSender<String> sender =
            new SafeMessageToSessionSenderImpl<>(messagingControl, transformer);

        sender.send(sessionId, "path", "value", sendCallback);

        verify(transformer).transform("value");
        verify(messagingControl).send(sessionId, "path", json, sendCallback);
    }

    @Test
    public void sendToFilter() throws Exception {
        final SafeMessageToSessionSender<String> sender =
            new SafeMessageToSessionSenderImpl<>(messagingControl, transformer);

        sender.sendToFilter("filter", "path", "value", sendToFilterCallback);

        verify(transformer).transform("value");
        verify(messagingControl).sendToFilter("filter", "path", json, sendToFilterCallback);
    }
}
