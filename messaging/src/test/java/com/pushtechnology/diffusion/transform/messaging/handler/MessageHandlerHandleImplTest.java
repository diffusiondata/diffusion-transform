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

package com.pushtechnology.diffusion.transform.messaging.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.features.RegisteredHandler;

/**
 * Unit tests for {@link MessageHandlerHandleImpl}.
 *
 * @author Push Technology Limited
 */
public final class MessageHandlerHandleImplTest {
    @Mock
    private RegisteredHandler registeredHandler;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void closeFirst() {
        final MessageHandlerHandleImpl handle = new MessageHandlerHandleImpl();

        handle.close();

        handle.setHandle(registeredHandler);

        verify(registeredHandler).close();
    }

    @Test
    public void closeLater() {
        final MessageHandlerHandleImpl handle = new MessageHandlerHandleImpl();

        handle.setHandle(registeredHandler);

        handle.close();

        verify(registeredHandler).close();
    }
}