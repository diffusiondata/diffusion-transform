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
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.FilteredRequestCallback;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.messaging.send.RequestToSessionSender.TransformedFilterCallback;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

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
 * Unit tests for {@link RequestToSessionSenderImpl}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class RequestToSessionSenderImplTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Session session;

    @Mock
    private MessagingControl messaging;

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

    @Mock
    private SessionId sessionId;

    @Mock
    private TransformedFilterCallback<JSON, String> callback;

    @Captor
    private ArgumentCaptor<FilteredRequestCallback<JSON>> callbackCaptor;

    @Before
    public void setUp() throws TransformationException {
        when(session.feature(MessagingControl.class)).thenReturn(messaging);

        when(messaging.sendRequest(sessionId, "path", request, JSON.class, JSON.class)).thenReturn(completedFuture(response));
        final CompletableFuture<JSON> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("for test"));
        when(messaging.sendRequest(sessionId, "path", failingRequest, JSON.class, JSON.class)).thenReturn(failedFuture);
        when(messaging.sendRequest(sessionId, "path", badDataRequest, JSON.class, JSON.class)).thenReturn(completedFuture(untransformableResponse));

        final CompletableFuture<Integer> numberSent = CompletableFuture.completedFuture(1);
        when(messaging.sendRequestToFilter(eq("filter"), eq("path"), eq(request), eq(JSON.class), eq(JSON.class), isNotNull(FilteredRequestCallback.class))).thenReturn(numberSent);

        when(requestTransformer.transform("good")).thenReturn(request);
        when(requestTransformer.transform("failing")).thenReturn(failingRequest);
        when(requestTransformer.transform("bad")).thenReturn(badDataRequest);
        when(requestTransformer.transform("exception")).thenThrow(new TransformationException(new RuntimeException("for test")));

        when(responseTransformer.transform(response)).thenReturn("good");
        when(responseTransformer.transform(untransformableResponse)).thenThrow(new TransformationException(new RuntimeException("for test")));
    }

    @Test
    public void sendGood() throws Exception {
        final RequestToSessionSender<JSON, String, String> sender = new RequestToSessionSenderImpl<>(
            session,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        assertEquals("good", sender.sendRequest(sessionId, "path", "good").get());
    }

    @Test
    public void sendFailed() throws Exception {
        final RequestToSessionSender<JSON, String, String> sender = new RequestToSessionSenderImpl<>(
            session,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        expectedException.expectCause(instanceOf(RuntimeException.class));
        sender.sendRequest(sessionId, "path", "failing").get();
    }

    @Test
    public void failToTransformRequest() throws Exception {
        final RequestToSessionSender<JSON, String, String> sender = new RequestToSessionSenderImpl<>(
            session,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        expectedException.expect(TransformationException.class);
        sender.sendRequest(sessionId, "path", "exception");
    }

    @Test
    public void failToTransformResponse() throws Exception {
        final RequestToSessionSender<JSON, String, String> sender = new RequestToSessionSenderImpl<>(
            session,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        expectedException.expectCause(instanceOf(TransformationException.class));
        sender.sendRequest(sessionId, "path", "bad").get();
    }

    @Test
    public void sendGoodToFilter() throws Exception {
        final RequestToSessionSender<JSON, String, String> sender = new RequestToSessionSenderImpl<>(
            session,
            JSON.class,
            JSON.class,
            requestTransformer,
            responseTransformer);

        assertEquals(1, (int) sender.sendRequest("filter", "path", "good", callback).get());
    }
}
