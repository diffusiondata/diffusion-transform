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

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.topics.TopicSelector;

/**
 * Abstract implementation of {@link StreamBuilder}.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @param <V> the type of the value steam to be built
 * @author Push Technology Limited
 */
/*package*/ abstract class AbstractStreamBuilder<S, T, V extends Topics.ValueStream<T>>
        implements StreamBuilder<S, T, V> {
    // CHECKSTYLE.OFF: VisibilityModifier
    /**
     * Source value type.
     */
    protected final Class<S> valueType;
    // CHECKSTYLE.ON: VisibilityModifier

    /**
     * Constructor.
     */
    protected AbstractStreamBuilder(Class<S> valueType) {
        this.valueType = valueType;
    }

    @Override
    public final void create(Topics topicsFeature, String topicSelector, V stream) {
        topicsFeature.addStream(topicSelector, valueType, adaptStream(stream));
    }

    @Override
    public final void create(Topics topicsFeature, TopicSelector topicSelector, V stream) {
        topicsFeature.addStream(topicSelector, valueType, adaptStream(stream));
    }

    @Override
    public void createFallback(Topics topicsFeature, V stream) {
        topicsFeature.addFallbackStream(valueType, adaptStream(stream));
    }

    /**
     * Adapt the target value stream to the source value stream.
     * @param targetStream The target value stream
     * @return The source value stream
     */
    protected abstract Topics.ValueStream<S> adaptStream(V targetStream);
}
