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

package com.pushtechnology.diffusion.transform.stream;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.TimeSeries.Event;
import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.features.Topics.UnsubscribeReason;
import com.pushtechnology.diffusion.client.features.Topics.ValueStream;
import com.pushtechnology.diffusion.client.session.Session;
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
 * @param <U> the type of the time series value stream to be built
 * @author Push Technology Limited
 */
/*package*/ abstract class AbstractStreamBuilder<S, T, V extends ValueStream<T>, U extends ValueStream<Event<T>>>
        implements StreamBuilder<S, T, V, U> {
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
    public final StreamHandle register(Topics topicsFeature, String topicSelector, V stream) {
        final ValueStream<S> valueStream = adaptStream(stream);
        topicsFeature.addStream(topicSelector, valueType, valueStream);
        return new StreamHandleImpl(topicsFeature, valueStream);
    }

    @Override
    public final StreamHandle register(Session session, String topicSelector, V stream) {
        return register(session.feature(Topics.class), topicSelector, stream);
    }

    @Override
    public final StreamHandle register(Topics topicsFeature, TopicSelector topicSelector, V stream) {
        final ValueStream<S> valueStream = adaptStream(stream);
        topicsFeature.addStream(topicSelector, valueType, valueStream);
        return new StreamHandleImpl(topicsFeature, valueStream);
    }

    @Override
    public final StreamHandle register(Session session, TopicSelector topicSelector, V stream) {
        return register(session.feature(Topics.class), topicSelector, stream);
    }

    @Override
    public final StreamHandle createFallback(Topics topicsFeature, V stream) {
        final DataType<S> dataType = Diffusion.dataTypes().getByClass(valueType);
        final TopicType topicType = TopicType.valueOf(dataType.getTypeName().toUpperCase());
        final ValueStream<S> valueStream = new FilterStream<>(topicType, adaptStream(stream));
        topicsFeature.addFallbackStream(valueType, valueStream);
        return new StreamHandleImpl(topicsFeature, valueStream);
    }

    @Override
    public final StreamHandle createFallback(Session session, V stream) {
        return createFallback(session.feature(Topics.class), stream);
    }

    @Override
    public final StreamHandle createTimeSeries(Session session, String topicSelector, U stream) {
        final ValueStream<Event<S>> valueStream = adaptTimeSeriesStream(stream);
        session.feature(Topics.class).addTimeSeriesStream(topicSelector, valueType, valueStream);
        return new StreamHandleImpl(session.feature(Topics.class), valueStream);
    }

    /**
     * Adapt the target value stream to the source value stream.
     * @param targetStream The target value stream
     * @return The source value stream
     */
    protected abstract ValueStream<S> adaptStream(V targetStream);

    /**
     * Adapt the target value stream to the source time series value stream.
     * @param targetStream The target value stream
     * @return The source value stream
     */
    protected abstract ValueStream<Event<S>> adaptTimeSeriesStream(U targetStream);

    /**
     * Implementation of {@link ValueStream} that filters by topic type. Used to restrict fallback streams to
     * certain topic types.
     *
     * @param <S>
     */
    private static final class FilterStream<S> implements ValueStream<S> {
        private final TopicType topicType;
        private final ValueStream<S> delegate;

        private FilterStream(TopicType topicType, ValueStream<S> delegate) {
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
