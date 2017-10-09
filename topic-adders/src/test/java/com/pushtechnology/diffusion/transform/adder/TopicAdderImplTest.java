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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Unit tests for {@link TopicAdderImpl}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class TopicAdderImplTest {
    @Mock
    private TopicControl topicControl;
    @Mock
    private TopicControl.AddCallback addCallback;
    @Mock
    private Transformer<String, Binary> transformer;
    @Mock
    private Binary binary;

    private TopicAdder<String> adder;

    @Before
    public void setUp() throws TransformationException {
        initMocks(this);

        adder = new TopicAdderImpl<>(topicControl, TopicType.BINARY, transformer);
        when(transformer.transform("value")).thenReturn(binary);
    }


    @Test
    public void addWithValue() {
        adder.add("topic", addCallback);

        verify(topicControl).addTopic("topic", TopicType.BINARY, addCallback);
    }

    @Test
    public void addWithoutValue() throws TransformationException {
        adder.add("topic", "value", addCallback);

        verify(topicControl).addTopic("topic", TopicType.BINARY, binary, addCallback);
        verify(transformer).transform("value");
    }
}
