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
import static org.mockito.Mockito.when;

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit tests for {@link RequestStreamAdapter}.
 *
 * @author Push Technology Limited
 */
public final class RequestStreamAdapterTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private InternalTransformer<JSON, JSON> requestTransformer;
    @Mock
    private InternalTransformer<JSON, JSON> responseTransformer;
    @Mock
    private TransformedRequestStream<JSON, JSON, JSON> delegate;
    @Mock
    private Messaging.RequestStream.Responder<JSON> responder;
    @Mock
    private JSON request;
    @Mock
    private JSON response;
    @Mock
    private JSON transformedRequest;
    @Mock
    private JSON transformedResponse;

    @Captor
    private ArgumentCaptor<TransformedRequestStream.Responder<JSON>> responderCaptor;

    @Before
    public void setUp() throws TransformationException {
        when(requestTransformer.transform(request)).thenReturn(transformedRequest);
        when(responseTransformer.transform(response)).thenReturn(transformedResponse);
    }

    @Test
    public void receiveRequest() throws TransformationException {
        final RequestStreamAdapter<JSON, JSON, JSON, JSON> adapter = new RequestStreamAdapter<>(
            requestTransformer,
            responseTransformer,
            delegate);

        adapter.onRequest("path", request, responder);

        verify(requestTransformer).transform(request);

        verify(delegate).onRequest(eq("path"), eq(transformedRequest), responderCaptor.capture());

        final TransformedRequestStream.Responder<JSON> transformedResponder = responderCaptor.getValue();

        transformedResponder.respond(response);
        verify(responseTransformer).transform(response);
        verify(responder).respond(transformedResponse);

        transformedResponder.reject("error");
        verify(responder).reject("error");
    }

    @Test
    public void receiveBadRequest() throws TransformationException {
        final TransformationException e = new TransformationException("for test");
        when(requestTransformer.transform(request)).thenThrow(e);

        final RequestStreamAdapter<JSON, JSON, JSON, JSON> adapter = new RequestStreamAdapter<>(
            requestTransformer,
            responseTransformer,
            delegate);

        adapter.onRequest("path", request, responder);

        verify(requestTransformer).transform(request);

        verify(delegate).onTransformationException(eq("path"), eq(request), responderCaptor.capture(), eq(e));

        final TransformedRequestStream.Responder<JSON> transformedResponder = responderCaptor.getValue();

        transformedResponder.respond(response);
        verify(responseTransformer).transform(response);
        verify(responder).respond(transformedResponse);

        transformedResponder.reject("error");
        verify(responder).reject("error");
    }

    @Test
    public void onError() {
        final RequestStreamAdapter<JSON, JSON, JSON, JSON> adapter = new RequestStreamAdapter<>(
            requestTransformer,
            responseTransformer,
            delegate);

        adapter.onError(ErrorReason.NO_SUCH_SESSION);
        verify(delegate).onError(ErrorReason.NO_SUCH_SESSION);
    }

    @Test
    public void onClose() {
        final RequestStreamAdapter<JSON, JSON, JSON, JSON> adapter = new RequestStreamAdapter<>(
            requestTransformer,
            responseTransformer,
            delegate);

        adapter.onClose();
        verify(delegate).onClose();
    }
}