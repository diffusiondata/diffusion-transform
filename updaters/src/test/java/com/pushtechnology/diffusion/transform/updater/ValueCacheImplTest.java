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

package com.pushtechnology.diffusion.transform.updater;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.topics.TopicSelector;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Unit tests for {@link ValueCacheImpl}.
 *
 * @author Push Technology Limited
 */
public final class ValueCacheImplTest {
    @Mock
    private TopicUpdateControl.ValueUpdater<JSON> delegateUpdater;
    @Mock
    private JSON jsonValue;

    private ValueCache<JSON> valueCache;

    @Before
    public void setUp() {
        initMocks(this);

        valueCache = new ValueCacheImpl<>(delegateUpdater);

        when(delegateUpdater.getCachedValue("topic")).thenReturn(jsonValue);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(jsonValue, delegateUpdater);
    }

    @Test
    public void getCachedValue() {
        final JSON cachedValue = valueCache.getCachedValue("topic");

        verify(delegateUpdater).getCachedValue("topic");

        assertEquals(jsonValue, cachedValue);
    }

    @Test
    public void removeCachedValues() {
        valueCache.removeCachedValues("topic");

        verify(delegateUpdater).removeCachedValues("topic");
    }

    @Test
    public void removeCachedValuesBySelector() {
        final TopicSelector selector = Diffusion.topicSelectors().parse("topic");

        valueCache.removeCachedValues(selector);

        verify(delegateUpdater).removeCachedValues(selector);
    }
}
