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

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.features.Topics.UnsubscribeReason;
import com.pushtechnology.diffusion.client.topics.TopicSelector;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.DataType;

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
    public final StreamHandle create(Topics topicsFeature, String topicSelector, V stream) {
        final Topics.ValueStream<S> valueStream = adaptStream(stream);
        topicsFeature.addStream(topicSelector, valueType, valueStream);
        return new StreamHandleImpl(topicsFeature, valueStream);
    }

    @Override
    public final StreamHandle create(Topics topicsFeature, TopicSelector topicSelector, V stream) {
        final Topics.ValueStream<S> valueStream = adaptStream(stream);
        topicsFeature.addStream(topicSelector, valueType, valueStream);
        return new StreamHandleImpl(topicsFeature, valueStream);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final StreamHandle createFallback(Topics topicsFeature, V stream) {
        final DataType<S> dataType = (DataType<S>) Diffusion.dataTypes().getByClass(valueType);
        final TopicType topicType = TopicType.valueOf(dataType.getTypeName().toUpperCase());
        final Topics.ValueStream<S> valueStream = new FilterStream<>(topicType, adaptStream(stream));
        topicsFeature.addFallbackStream(valueType, valueStream);
        return new StreamHandleImpl(topicsFeature, valueStream);
    }

    /**
     * Adapt the target value stream to the source value stream.
     * @param targetStream The target value stream
     * @return The source value stream
     */
    protected abstract Topics.ValueStream<S> adaptStream(V targetStream);

    /**
     * Implementation of {@link Topics.ValueStream} that filters by topic type. Used to restrict fallback streams to
     * certain topic types.
     *
     * @param <S>
     */
    private static final class FilterStream<S> implements Topics.ValueStream<S> {
        private final TopicType topicType;
        private final Topics.ValueStream<S> delegate;

        private FilterStream(TopicType topicType, Topics.ValueStream<S> delegate) {
            this.topicType = topicType;
            this.delegate = delegate;
        }

        @Override
        public void onValue(String topicPath, TopicSpecification topicSpecification, S oldValue, S newValue) {
            if (topicType.equals(topicSpecification.getType())) {
                delegate.onValue(topicPath, topicSpecification, oldValue, newValue);
            }
        }

        @Override
        public void onSubscription(String topicPath, TopicSpecification topicSpecification) {
            if (topicType.equals(topicSpecification.getType())) {
                delegate.onSubscription(topicPath, topicSpecification);
            }
        }

        @Override
        public void onUnsubscription(
            String topicPath,
            TopicSpecification topicSpecification,
            UnsubscribeReason unsubscribeReason) {

            if (topicType.equals(topicSpecification.getType())) {
                delegate.onUnsubscription(topicPath, topicSpecification, unsubscribeReason);
            }
        }

        @Override
        public void onClose() {
            delegate.onClose();
        }

        @Override
        public void onError(ErrorReason errorReason) {
            delegate.onError(errorReason);
        }
    }
}
