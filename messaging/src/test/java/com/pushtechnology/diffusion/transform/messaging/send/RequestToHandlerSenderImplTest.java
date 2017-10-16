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

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Unit tests for {@link RequestToHandlerSenderImpl}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class RequestToHandlerSenderImplTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Messaging messaging;

    @Mock
    private Transformer<String, JSON> requestTransformer;

    @Mock
    private Transformer<JSON, String> responseTransformer;

    @Mock
    private JSON request;

    @Mock
    private JSON response;

    @Mock
    private JSON failingRequest;

    @Mock
    private JSON badDataRequest;

    @Mock
    private JSON untransformableResponse;

    @Before
    public void setUp() throws TransformationException {
        when(messaging.sendRequest("path", request, JSON.class, JSON.class)).thenReturn(completedFuture(response));
        final CompletableFuture<JSON> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("for test"));
        when(messaging.sendRequest("path", failingRequest, JSON.class, JSON.class)).thenReturn(failedFuture);
        when(messaging.sendRequest("path", badDataRequest, JSON.class, JSON.class)).thenReturn(completedFuture(untransformableResponse));

        when(requestTransformer.transform("good")).thenReturn(request);
        when(requestTransformer.transform("failing")).thenReturn(failingRequest);
        when(requestTransformer.transform("bad")).thenReturn(badDataRequest);
        when(requestTransformer.transform("exception")).thenThrow(new TransformationException(new RuntimeException("for test")));

        when(responseTransformer.transform(response)).thenReturn("good");
        when(responseTransformer.transform(untransformableResponse)).thenThrow(new TransformationException(new RuntimeException("for test")));
    }

    @Test
    public void sendGood() throws Exception {
        final RequestToHandlerSender<String, String> sender = new RequestToHandlerSenderImpl<>(
            messaging,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        assertEquals("good", sender.sendRequest("path", "good").get());
    }

    @Test
    public void sendFailed() throws Exception {
        final RequestToHandlerSender<String, String> sender = new RequestToHandlerSenderImpl<>(
            messaging,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        expectedException.expectCause(instanceOf(RuntimeException.class));
        sender.sendRequest("path", "failing").get();
    }

    @Test
    public void failToTransformRequest() throws Exception {
        final RequestToHandlerSender<String, String> sender = new RequestToHandlerSenderImpl<>(
            messaging,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        expectedException.expect(TransformationException.class);
        sender.sendRequest("path", "exception");
    }

    @Test
    public void failToTransformResponse() throws Exception {
        final RequestToHandlerSender<String, String> sender = new RequestToHandlerSenderImpl<>(
            messaging,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        expectedException.expectCause(instanceOf(TransformationException.class));
        sender.sendRequest("path", "bad").get();
    }
}
