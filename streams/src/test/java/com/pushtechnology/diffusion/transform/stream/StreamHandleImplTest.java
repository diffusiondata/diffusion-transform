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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.callbacks.Stream;
import com.pushtechnology.diffusion.client.features.Topics;

/**
 * Unit tests for {@link StreamHandleImpl}.
 *
 * @author Push Technology Limited
 */
public final class StreamHandleImplTest {
    @Mock
    private Topics topicsFeature;
    @Mock
    private Stream stream;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(topicsFeature, stream);
    }

    @Test
    public void close() {
        final StreamHandle streamHandle = new StreamHandleImpl(topicsFeature, stream);
        streamHandle.close();
        verify(topicsFeature).removeStream(stream);
    }
}
