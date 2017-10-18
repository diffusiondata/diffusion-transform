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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 * Unit tests for {@link BoundRequestStreamBuilderImpl}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class BoundRequestStreamBuilderImplTest {

    @Mock
    private SafeTransformer<JSON, String> requestTransformer;
    @Mock
    private SafeTransformer<String, JSON> responseTransformer;
    @Mock
    private UnsafeTransformer<String, String> stringTransformer;
    @Mock
    private Session session;
    @Mock
    private Messaging messaging;
    @Mock
    private JSON json;
    @Mock
    private TransformedRequestStream<JSON, String, String> requestStream;

    @Captor
    private ArgumentCaptor<Messaging.RequestStream<JSON, JSON>> streamCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(responseTransformer.transform("value")).thenReturn(json);
        when(stringTransformer.transform("value")).thenReturn("value");
        when(session.feature(Messaging.class)).thenReturn(messaging);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(
            responseTransformer,
            json,
            messaging,
            session);
    }

    @Test
    public void transformRequest() throws Exception {
        final BoundRequestStreamBuilder<JSON, String, String> builder = new BoundRequestStreamBuilderImpl<>(
            session,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer)
            .transformRequest(stringTransformer);

        builder.setStream("path", requestStream);

        verify(session).feature(Messaging.class);
        verify(messaging).setRequestStream(eq("path"), eq(JSON.class), eq(JSON.class), streamCaptor.capture());
    }

    @Test
    public void transformResponse() throws Exception {
        final BoundRequestStreamBuilder<JSON, String, String> builder = new BoundRequestStreamBuilderImpl<>(
            session,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer)
            .transformResponse(stringTransformer);

        builder.setStream("path", requestStream);

        verify(session).feature(Messaging.class);
        verify(messaging).setRequestStream(eq("path"), eq(JSON.class), eq(JSON.class), streamCaptor.capture());
    }

}
