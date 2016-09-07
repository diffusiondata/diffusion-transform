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

import static com.pushtechnology.diffusion.client.features.Topics.UnsubscribeReason.REQUESTED;
import static com.pushtechnology.diffusion.client.session.Session.State.CLOSED_BY_CLIENT;
import static com.pushtechnology.diffusion.client.session.Session.State.CONNECTED_ACTIVE;
import static com.pushtechnology.diffusion.client.session.Session.State.CONNECTING;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

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
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * Integration test for JSON streams.
 * @author Push Technology Limited
 */
public final class JSONStreamIT {
    @Mock
    private Session.Listener listener;
    @Mock
    private TransformedStream<JSON, Map<String, String>> stream;
    @Mock
    private TopicControl.AddCallback addCallback;
    @Mock
    private TopicControl.RemoveCallback removeCallback;
    @Mock
    private Topics.CompletionCallback completionCallback;
    @Mock
    private TopicUpdateControl.Updater.UpdateCallback updateCallback;
    @Captor
    private ArgumentCaptor<TopicSpecification> specificationCaptor;
    @Captor
    private ArgumentCaptor<Map<String, String>> valueCaptor;

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
        session.feature(TopicControl.class).removeTopics("test", removeCallback);
        verify(removeCallback, timed()).onTopicsRemoved();

        session.close();

        verify(listener, timed()).onSessionStateChanged(session, CONNECTED_ACTIVE, CLOSED_BY_CLIENT);

        verifyNoMoreInteractions(listener, stream, addCallback, removeCallback, completionCallback, updateCallback);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void fallback() {
        final Topics topics = session.feature(Topics.class);
        final StreamHandle streamHandle = StreamBuilders
            .newJsonStreamBuilder()
            .transform(Transformers.toMapOf(String.class))
            .createFallback(topics, stream);

        topics.subscribe("?test//", completionCallback);
        verify(completionCallback, timed()).onComplete();

        final TopicControl topicControl = session.feature(TopicControl.class);
        topicControl.addTopic("test/topic", TopicType.JSON, addCallback);
        verify(addCallback, timed()).onTopicAdded("test/topic");

        verify(stream, timed()).onSubscription(eq("test/topic"), specificationCaptor.capture());
        final TopicSpecification specification0 = specificationCaptor.getValue();
        assertEquals(TopicType.JSON, specification0.getType());

        final TopicUpdateControl.ValueUpdater<JSON> updater = session
            .feature(TopicUpdateControl.class)
            .updater()
            .valueUpdater(JSON.class);

        updater.update("test/topic", Diffusion.dataTypes().json().fromJsonString("{}"), updateCallback);
        verify(updateCallback, timed()).onSuccess();

        verify(stream, timed())
            .onValue(eq("test/topic"), specificationCaptor.capture(), isNull(Map.class), valueCaptor.capture());
        final Map<String, String> value = valueCaptor.getValue();
        assertEquals(emptyMap(), value);

        topics.unsubscribe("?test//", completionCallback);
        verify(completionCallback, timed().times(2)).onComplete();

        verify(stream, timed()).onUnsubscription(eq("test/topic"), specificationCaptor.capture(), eq(REQUESTED));
        final TopicSpecification specification1 = specificationCaptor.getValue();
        assertEquals(specification0, specification1);

        streamHandle.close();
        verify(stream, timed()).onClose();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void stream() {
        final Topics topics = session.feature(Topics.class);
        final StreamHandle streamHandle = StreamBuilders
            .newJsonStreamBuilder()
            .transform(Transformers.toMapOf(String.class))
            .register(topics, "?test//", stream);

        topics.subscribe("?test//", completionCallback);
        verify(completionCallback, timed()).onComplete();

        final TopicControl topicControl = session.feature(TopicControl.class);
        topicControl.addTopic("test/topic", TopicType.JSON, addCallback);
        verify(addCallback, timed()).onTopicAdded("test/topic");

        verify(stream, timed()).onSubscription(eq("test/topic"), specificationCaptor.capture());
        final TopicSpecification specification0 = specificationCaptor.getValue();
        assertEquals(TopicType.JSON, specification0.getType());

        final TopicUpdateControl.ValueUpdater<JSON> updater = session
            .feature(TopicUpdateControl.class)
            .updater()
            .valueUpdater(JSON.class);

        updater.update("test/topic", Diffusion.dataTypes().json().fromJsonString("{}"), updateCallback);
        verify(updateCallback, timed()).onSuccess();

        verify(stream, timed())
            .onValue(eq("test/topic"), specificationCaptor.capture(), isNull(Map.class), valueCaptor.capture());
        final Map<String, String> value = valueCaptor.getValue();
        assertEquals(emptyMap(), value);

        topics.unsubscribe("?test//", completionCallback);
        verify(completionCallback, timed().times(2)).onComplete();

        verify(stream, timed()).onUnsubscription(eq("test/topic"), specificationCaptor.capture(), eq(REQUESTED));
        final TopicSpecification specification1 = specificationCaptor.getValue();
        assertEquals(specification0, specification1);

        streamHandle.close();
        verify(stream, timed()).onClose();
    }

    private VerificationWithTimeout timed() {
        return timeout(5000L);
    }
}
