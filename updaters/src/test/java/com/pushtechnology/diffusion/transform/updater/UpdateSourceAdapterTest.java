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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.callbacks.Registration;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Unit tests for {@link UpdateSourceAdapter}.
 *
 * @author Push Technology Limited
 */
public final class UpdateSourceAdapterTest {

    @Mock
    private Registration registration;
    @Mock
    private TopicUpdateControl.Updater updater;
    @Mock
    private TransformedUpdater<JSON, JSON> valueUpdater;
    @Mock
    private ValueCache valueCache;
    @Mock
    private TransformedUpdateSource<JSON, JSON, TransformedUpdater<JSON, JSON>> updateSource;
    @Mock
    private InternalUpdaterBuilder<JSON, JSON> updaterBuilder;

    private UpdateSourceAdapter<JSON, JSON> adapter;

    @Before
    public void setUp() {
        initMocks(this);

        adapter = new UpdateSourceAdapter<>(valueCache, updaterBuilder, updateSource);

        when(updaterBuilder.create(updater)).thenReturn(valueUpdater);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(valueCache, updaterBuilder, updateSource, registration, updater);
    }

    @Test
    public void onActive() {
        adapter.onActive("topic/path", updater);

        verify(updaterBuilder).create(updater);
        verify(updateSource).onActive("topic/path", valueUpdater);
        verify(valueCache).removeCachedValues("topic/path");
    }

    @Test
    public void onStandby() {
        adapter.onStandby("topic/path");

        verify(updateSource).onStandby("topic/path");
    }

    @Test
    public void onRegistered() {
        adapter.onRegistered("topic/path", registration);

        verify(updateSource).onRegistered("topic/path", registration);
    }

    @Test
    public void onClose() {
        adapter.onClose("topic/path");

        verify(updateSource).onClose("topic/path");
        verify(valueCache).removeCachedValues("topic/path");
    }

    @Test
    public void onError() {
        adapter.onError("topic/path", ErrorReason.COMMUNICATION_FAILURE);

        verify(updateSource).onError("topic/path", ErrorReason.COMMUNICATION_FAILURE);
        verify(valueCache).removeCachedValues("topic/path");
    }
}
