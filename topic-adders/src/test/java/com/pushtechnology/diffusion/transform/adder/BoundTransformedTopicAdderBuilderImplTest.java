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

import static com.pushtechnology.diffusion.client.topics.details.TopicType.BINARY;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.bigIntegerToBinary;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.identity;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Unit tests for {@link BoundSafeTopicAdderBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class BoundTransformedTopicAdderBuilderImplTest {
    @Mock
    private TopicControl control;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(control);
    }

    @Test
    public void transform() {
        final BoundTransformedTopicAdderBuilder<Binary, BigInteger> builder =
            new BoundTransformedTopicAdderBuilderImpl<>(BINARY, identity(Binary.class), control)
                .transform(bigIntegerToBinary());

        assertNotNull(builder);
    }

    @Test
    public void transformToClass() {
        final BoundTransformedTopicAdderBuilder<Binary, BigInteger> builder =
            new BoundTransformedTopicAdderBuilderImpl<>(BINARY, identity(Binary.class), control)
                .transform(bigIntegerToBinary(), BigInteger.class);

        assertNotNull(builder);
    }

    @Test
    public void transformWith() {
        final BoundTransformedTopicAdderBuilder<Binary, String> builder =
            new BoundTransformedTopicAdderBuilderImpl<>(BINARY, identity(Binary.class), control)
                .transformWith(new UnsafeTransformer<String, Binary>() {
                    @Override
                    public Binary transform(String value) throws Exception {
                        return null;
                    }
                });

        assertNotNull(builder);
    }

    @Test
    public void transformWithToClass() {
        final BoundTransformedTopicAdderBuilder<Binary, String> builder =
            new BoundTransformedTopicAdderBuilderImpl<>(BINARY, identity(Binary.class), control)
                .transformWith(new UnsafeTransformer<String, Binary>() {
                    @Override
                    public Binary transform(String value) throws Exception {
                        return null;
                    }
                }, String.class);

        assertNotNull(builder);
    }

    @Test
    public void create() {
        final BoundTransformedTopicAdderBuilder<Binary, BigInteger> builder =
            new BoundTransformedTopicAdderBuilderImpl<>(BINARY, bigIntegerToBinary(), control);

        final TopicAdder<BigInteger> adder = builder.create();

        assertNotNull(adder);
    }
}
