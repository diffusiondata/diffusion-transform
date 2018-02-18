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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import java.util.function.Function;

import com.pushtechnology.diffusion.client.features.TimeSeries.Event;
import com.pushtechnology.diffusion.client.features.TimeSeries.EventMetadata;
import com.pushtechnology.diffusion.client.features.Topics.ValueStream;
import com.pushtechnology.diffusion.timeseries.event.EventImpl;
import com.pushtechnology.diffusion.timeseries.event.EventMetadataImpl;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A {@link SafeStreamBuilder} that does register a transformed stream.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
/*package*/ final class SafeStreamBuilderImpl<S, T>
        extends AbstractStreamBuilder<S, T, ValueStream<T>, ValueStream<Event<T>>>
        implements SafeStreamBuilder<S, T> {
    private final Function<S, T> transformer;

    /**
     * Constructor.
     */
    /*package*/ SafeStreamBuilderImpl(Class<S> valueType, Function<S, T> transformer) {
        super(valueType);
        this.transformer = transformer;
    }

    @Override
    public <R> StreamBuilder<S, R, TransformedStream<S, R>, TransformedStream<Event<S>, Event<R>>>
        unsafeTransform(UnsafeTransformer<T, R> newTransformer) {

        return new StreamBuilderImpl<>(valueType, toTransformer(transformer).chainUnsafe(newTransformer));
    }


    @Override
    public <R> SafeStreamBuilder<S, R> transform(Function<T, R> newTransformer) {
        return new SafeStreamBuilderImpl<>(valueType, transformer.andThen(newTransformer));
    }

    @Override
    protected ValueStream<S> adaptStream(ValueStream<T> targetStream) {
        return new SafeStreamAdapter<>(transformer, targetStream);
    }

    @Override
    protected ValueStream<Event<S>> adaptTimeSeriesStream(ValueStream<Event<T>> targetStream) {
        final Function<Event<S>, Event<T>> eventTransformer = value -> {
            final T newValue = transformer.apply(value.value());
            final EventMetadata metadata = new EventMetadataImpl(
                value.sequence(),
                value.timestamp(),
                value.author());
            return EventImpl.createEvent(metadata, value.isEditEvent() ? value.originalEvent() : metadata, newValue);
        };
        return new SafeStreamAdapter<>(eventTransformer, targetStream);
    }
}
