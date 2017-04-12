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

package com.pushtechnology.diffusion.transform.messaging.stream;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.transform.messaging.stream.MessageStreamHandle;
import com.pushtechnology.diffusion.transform.messaging.stream.MessageStreamHandleImpl;

/**
 * Unit tests for {@link MessageStreamHandleImpl}.
 *
 * @author Push Technology Limited
 */
public final class MessageStreamHandleImplTest {
    @Mock
    private Messaging messaging;
    @Mock
    private Messaging.MessageStream stream;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void close() {
        final MessageStreamHandle handle = new MessageStreamHandleImpl(messaging, stream);

        handle.close();

        verify(messaging).removeMessageStream(stream);
    }

}