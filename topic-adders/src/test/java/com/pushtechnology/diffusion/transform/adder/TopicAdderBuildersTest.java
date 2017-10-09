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

package com.pushtechnology.diffusion.transform.adder;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Unit tests for {@link TopicAdderBuilders}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class TopicAdderBuildersTest {

    @Test
    public void jsonTopicAdderBuilder() {
        final UnboundSafeTopicAdderBuilder<JSON, JSON> builder = TopicAdderBuilders.jsonTopicAdderBuilder();

        assertTrue(builder instanceof UnboundSafeTopicAdderBuilderImpl);
    }

    @Test
    public void binaryTopicAdderBuilder() {
        final UnboundSafeTopicAdderBuilder<Binary, Binary> builder = TopicAdderBuilders.binaryTopicAdderBuilder();

        assertTrue(builder instanceof UnboundSafeTopicAdderBuilderImpl);
    }
}
