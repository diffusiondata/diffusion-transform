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

package com.pushtechnology.diffusion.transform.adder;

import static com.pushtechnology.diffusion.client.session.Session.State.CLOSED_BY_CLIENT;
import static com.pushtechnology.diffusion.client.session.Session.State.CONNECTED_ACTIVE;
import static com.pushtechnology.diffusion.client.session.Session.State.CONNECTING;
import static com.pushtechnology.diffusion.client.topics.details.TopicType.JSON;
import static com.pushtechnology.diffusion.transform.adder.TopicAdderBuilders.jsonTopicAdderBuilder;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.fromMap;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toMapOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.verification.VerificationWithTimeout;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * Integration test for JSON topic adders.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class AddJSONTopicIT {
    @Mock
    private Session.Listener listener;
    @Mock
    private Topics.ValueStream<JSON> stream;
    @Mock
    private TopicControl.AddCallback addCallback;
    @Mock
    private TopicControl.RemovalCallback removalCallback;
    @Mock
    private Topics.CompletionCallback completionCallback;
    @Captor
    private ArgumentCaptor<TopicSpecification> specificationCaptor;
    @Captor
    private ArgumentCaptor<JSON> valueCaptor;

    private Session session;

    @Before
    public void setUp() {
        initMocks(this);
        session = Diffusion
            .sessions()
            .listener(listener)
            .principal("control")
            .password("password")
            .open("ws://localhost:8080");
        verify(listener, timed()).onSessionStateChanged(session, CONNECTING, CONNECTED_ACTIVE);
    }

    @After
    public void postConditions() {
        session.feature(TopicControl.class).remove("?test//", removalCallback);
        verify(removalCallback, timed()).onTopicsRemoved();

        session.close();

        verify(listener, timed()).onSessionStateChanged(session, CONNECTED_ACTIVE, CLOSED_BY_CLIENT);

        verifyNoMoreInteractions(listener, stream, addCallback, removalCallback, completionCallback);
    }

    @Test
    public void addWithoutValue() throws TransformationException {
        final Topics topics = session.feature(Topics.class);
        topics.addStream("?test//", JSON.class, stream);

        topics.subscribe("?test//", completionCallback);
        verify(completionCallback, timed()).onComplete();

        final TopicAdder<Map<String, Object>> topicAdder = jsonTopicAdderBuilder()
            .transform(fromMap())
            .bind(session)
            .create();

        topicAdder.add("test/topic", addCallback);

        verify(addCallback, timed()).onTopicAdded("test/topic");
        verify(stream, timed()).onSubscription(eq("test/topic"), specificationCaptor.capture());

        assertEquals(JSON, specificationCaptor.getValue().getType());

        topics.removeStream(stream);
        verify(stream, timed()).onClose();
    }

    @Test
    public void addWithValue() throws TransformationException {
        final Topics topics = session.feature(Topics.class);
        topics.addStream("?test//", JSON.class, stream);

        topics.subscribe("?test//", completionCallback);
        verify(completionCallback, timed()).onComplete();

        final TopicAdder<Map<String, String>> topicAdder = jsonTopicAdderBuilder()
            .transform(Transformers.<String>fromMap())
            .bind(session)
            .create();

        final Map<String, String> value = new HashMap<>();
        value.put("key", "value");

        topicAdder.add("test/topic", value, addCallback);

        verify(addCallback, timed()).onTopicAdded("test/topic");
        verify(stream, timed()).onSubscription(eq("test/topic"), specificationCaptor.capture());

        final TopicSpecification specification = specificationCaptor.getValue();
        assertEquals(JSON, specification.getType());

        verify(stream, timed()).onValue(
            eq("test/topic"),
            specificationCaptor.capture(),
            isNull(JSON.class),
            valueCaptor.capture());

        assertEquals(specification, specificationCaptor.getValue());
        assertEquals(value, toMapOf(String.class).transform(valueCaptor.getValue()));

        topics.removeStream(stream);
        verify(stream, timed()).onClose();
    }

    private VerificationWithTimeout timed() {
        return timeout(5000L);
    }
}
