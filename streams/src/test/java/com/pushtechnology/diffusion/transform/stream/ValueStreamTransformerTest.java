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

import static java.util.function.Function.identity;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link SafeStreamAdapter}.
 *
 * @author Push Technology Limited
 */
public final class ValueStreamTransformerTest {
    @Mock
    private Topics.ValueStream<String> delegate;
    @Mock
    private TopicSpecification specification;

    @Before
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void onValue() {
        final Topics.ValueStream<String> stream = new SafeStreamAdapter<>(identity(), delegate);

        stream.onValue("path", specification, null, "first");

        verify(delegate).onValue("path", specification, null, "first");

        stream.onValue("path", specification, "first", "second");

        verify(delegate).onValue("path", specification, "first", "second");
    }

    @Test
    public void onSubscription() {
        final Topics.ValueStream<String> stream = new SafeStreamAdapter<>(identity(), delegate);

        stream.onSubscription("path", specification);

        verify(delegate).onSubscription("path", specification);
    }

    @Test
    public void onUnsubscription() {
        final Topics.ValueStream<String> stream = new SafeStreamAdapter<>(identity(), delegate);

        stream.onUnsubscription("path", specification, Topics.UnsubscribeReason.REQUESTED);

        verify(delegate).onUnsubscription("path", specification, Topics.UnsubscribeReason.REQUESTED);
    }

    @Test
    public void onClose() {
        final Topics.ValueStream<String> stream = new SafeStreamAdapter<>(identity(), delegate);

        stream.onClose();

        verify(delegate).onClose();
    }

    @Test
    public void onError() {
        final Topics.ValueStream<String> stream = new SafeStreamAdapter<>(identity(), delegate);

        stream.onError(ErrorReason.TOPIC_TREE_REGISTRATION_CONFLICT);

        verify(delegate).onError(ErrorReason.TOPIC_TREE_REGISTRATION_CONFLICT);
    }
}
