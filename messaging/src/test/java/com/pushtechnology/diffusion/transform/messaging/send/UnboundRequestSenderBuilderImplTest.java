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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link UnboundRequestSenderBuilderImpl}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class UnboundRequestSenderBuilderImplTest {
    @Mock
    private SafeTransformer<String, JSON> transformer;
    @Mock
    private SafeTransformer<JSON, String> responseTransformer;
    @Mock
    private Transformer<String, String> stringTransformer;
    @Mock
    private UnsafeTransformer<String, String> unsafeTransformer;
    @Mock
    private Messaging messaging;
    @Mock
    private JSON json;
    @Mock
    private Session session;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(transformer.transform("value")).thenReturn(json);
        when(stringTransformer.transform("value")).thenReturn("value");
        when(unsafeTransformer.transform("value")).thenReturn("value");
        when(session.feature(Messaging.class)).thenReturn(messaging);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(
            transformer,
            unsafeTransformer,
            json,
            session);
    }

    @Test
    public void transformRequestAndSend() throws Exception {
        final UnboundRequestSenderBuilder<JSON, String, String> builder = new UnboundRequestSenderBuilderImpl<>(
            JSON.class,
            JSON.class,
            transformer,
            responseTransformer)
            .transformRequest(stringTransformer);

        final RequestToHandlerSender<String, String> sender = builder.buildToHandlerSender(session);
        verify(session).feature(Messaging.class);

        assertNotNull(sender);
    }

    @Test
    public void transformRequestWithAndSend() throws Exception {
        final UnboundRequestSenderBuilder<JSON, String, String> builder = new UnboundRequestSenderBuilderImpl<>(
            JSON.class,
            JSON.class,
            transformer,
            responseTransformer)
            .transformRequestWith(unsafeTransformer);

        final RequestToHandlerSender<String, String> sender = builder.buildToHandlerSender(session);
        verify(session).feature(Messaging.class);

        assertNotNull(sender);
    }

    @Test
    public void transformResponse() throws Exception {
        final UnboundRequestSenderBuilder<JSON, String, String> builder = new UnboundRequestSenderBuilderImpl<>(
            JSON.class,
            JSON.class,
            transformer,
            responseTransformer)
            .transformResponse(stringTransformer);

        final RequestToHandlerSender<String, String> sender = builder.buildToHandlerSender(session);
        verify(session).feature(Messaging.class);

        assertNotNull(sender);
    }

    @Test
    public void transformResponseWith() throws Exception {
        final UnboundRequestSenderBuilder<JSON, String, String> builder = new UnboundRequestSenderBuilderImpl<>(
            JSON.class,
            JSON.class,
            transformer,
            responseTransformer)
            .transformResponseWith(unsafeTransformer);

        final RequestToHandlerSender<String, String> sender = builder.buildToHandlerSender(session);
        verify(session).feature(Messaging.class);

        assertNotNull(sender);
    }

    @Test
    public void bind() throws Exception {
        final BoundRequestSenderBuilder<JSON, String, String> builder = new UnboundRequestSenderBuilderImpl<>(
            JSON.class,
            JSON.class,
            transformer,
            responseTransformer)
            .bind(session);

        assertNotNull(builder);
    }
}