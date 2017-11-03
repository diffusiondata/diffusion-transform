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

package com.pushtechnology.diffusion.transform.stream;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Unit tests for {@link StreamAdapter}.
 *
 * @author Push Technology Limited
 */
public final class StreamAdapterTest {

    @Mock
    private TransformedStream<String, String> delegate;
    @Mock
    private TopicSpecification specification;

    @Before
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void onValue() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> value, delegate);

        stream.onValue("path", specification, null, "first");

        verify(delegate).onValue("path", specification, null, "first");

        stream.onValue("path", specification, "first", "second");

        verify(delegate).onValue("path", specification, "first", "second");
    }

    @Test
    public void onValueMultipleTopics() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> value, delegate);

        stream.onValue("pathOne", specification, null, "first");
        stream.onValue("pathTwo", specification, null, "ay");

        verify(delegate).onValue("pathOne", specification, null, "first");
        verify(delegate).onValue("pathTwo", specification, null, "ay");

        stream.onValue("pathOne", specification, "first", "second");
        stream.onValue("pathTwo", specification, "ay", "bee");

        verify(delegate).onValue("pathOne", specification, "first", "second");
        verify(delegate).onValue("pathTwo", specification, "ay", "bee");
    }

    @Test
    public void onTransformationException() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> {
            throw new TransformationException(new Exception("Intentionally thrown in test"));
        }, delegate);

        stream.onValue("path", specification, null, "first");

        verify(delegate)
            .onTransformationException(eq("path"), eq(specification), eq("first"), isA(TransformationException.class));
    }

    @Test
    public void onRuntimeException() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> {
            throw new RuntimeException("Intentionally thrown in test");
        }, delegate);

        stream.onValue("path", specification, null, "first");

        verify(delegate)
            .onTransformationException(eq("path"), eq(specification), eq("first"), isA(TransformationException.class));
    }

    @Test
    public void onDelegateException() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                throw new RuntimeException("Intentionally thrown in test");
            }
        }).when(delegate).onValue(eq("path"), eq(specification), isNull(String.class), eq("first"));

        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> value, delegate);

        stream.onValue("path", specification, null, "first");
    }

    @Test
    public void onSubscription() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> value, delegate);

        stream.onSubscription("path", specification);

        verify(delegate).onSubscription("path", specification);
    }

    @Test
    public void onUnsubscription() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> value, delegate);

        stream.onUnsubscription("path", specification, Topics.UnsubscribeReason.REQUESTED);

        verify(delegate).onUnsubscription("path", specification, Topics.UnsubscribeReason.REQUESTED);
    }

    @Test
    public void onClose() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> value, delegate);

        stream.onClose();

        verify(delegate).onClose();
    }

    @Test
    public void onError() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> value, delegate);

        stream.onError(ErrorReason.TOPIC_TREE_REGISTRATION_CONFLICT);

        verify(delegate).onError(ErrorReason.TOPIC_TREE_REGISTRATION_CONFLICT);
    }

    @Test
    public void onSessionClose() {
        final Topics.ValueStream<String> stream = new StreamAdapter<>(value -> value, delegate);

        stream.onError(ErrorReason.SESSION_CLOSED);

        verify(delegate).onClose();
    }
}
