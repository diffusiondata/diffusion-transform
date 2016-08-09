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

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;

import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Unit tests for {@link StreamBuilders}.
 *
 * @author Push Technology Limited
 */
public final class StreamBuildersTest {

    @Before
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void newBinaryStreamBuilder() {
        final SafeStreamBuilder<Binary, Binary> streamBuilder = StreamBuilders.newBinaryStreamBuilder();
        assertTrue(streamBuilder instanceof SafeStreamBuilderImpl);
    }

    @Test
    public void newJsonStreamBuilder() {
        final SafeStreamBuilder<JSON, JSON> streamBuilder = StreamBuilders.newJsonStreamBuilder();
        assertTrue(streamBuilder instanceof SafeStreamBuilderImpl);
    }

    @Test
    public void newStreamBuilder() {
        final SafeStreamBuilder<JSON, JSON> streamBuilder = StreamBuilders.newStreamBuilder(JSON.class);
        assertTrue(streamBuilder instanceof SafeStreamBuilderImpl);
    }
}
