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

package com.pushtechnology.diffusion.transform.updater;

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link SafeUpdaterBuilder}.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
/*package*/ final class UnboundSafeUpdaterBuilderImpl<S, T> implements UnboundSafeUpdaterBuilder<S, T> {
    private final Class<S> valueType;
    private final SafeTransformer<T, S> transformer;

    UnboundSafeUpdaterBuilderImpl(Class<S> valueType, SafeTransformer<T, S> transformer) {
        this.valueType = valueType;
        this.transformer = transformer;
    }

    @Override
    public <R> UnboundTransformedUpdaterBuilder<S, R> transform(Transformer<R, T> newTransformer) {
        return new UnboundTransformedUpdaterBuilderImpl<>(valueType, chain(newTransformer, transformer));
    }

    @Override
    public <R> UnboundTransformedUpdaterBuilder<S, R> transform(Transformer<R, T> newTransformer, Class<R> type) {
        return new UnboundTransformedUpdaterBuilderImpl<>(valueType, chain(newTransformer, transformer));
    }

    @Override
    public <R> UnboundTransformedUpdaterBuilder<S, R> transformWith(UnsafeTransformer<R, T> newTransformer) {
        return new UnboundTransformedUpdaterBuilderImpl<>(valueType, chain(toTransformer(newTransformer), transformer));
    }

    @Override
    public <R> UnboundTransformedUpdaterBuilder<S, R> transformWith(
            UnsafeTransformer<R, T> newTransformer,
            Class<R> type) {
        return new UnboundTransformedUpdaterBuilderImpl<>(valueType, chain(toTransformer(newTransformer), transformer));
    }

    @Override
    public <R> UnboundSafeUpdaterBuilder<S, R> transform(SafeTransformer<R, T> newTransformer) {
        return new UnboundSafeUpdaterBuilderImpl<>(valueType, chain(newTransformer, transformer));
    }

    @Override
    public <R> UnboundSafeUpdaterBuilder<S, R> transform(SafeTransformer<R, T> newTransformer, Class<R> type) {
        return new UnboundSafeUpdaterBuilderImpl<>(valueType, chain(newTransformer, transformer));
    }

    @Override
    public SafeTransformedUpdater<S, T> create(TopicUpdateControl.Updater updater) {
        return new SafeTransformedUpdaterImpl<>(updater.valueUpdater(valueType), transformer);
    }

    // CHECKSTYLE.OFF: MissingDeprecated
    @Deprecated
    @Override
    public BoundSafeUpdaterBuilder<S, T> bind(TopicUpdateControl updateControl) {
        return new BoundSafeUpdaterBuilderImpl<>(updateControl, valueType, transformer);
    }
    // CHECKSTYLE.ON: MissingDeprecated

    @Override
    public BoundSafeUpdaterBuilder<S, T> bind(Session session) {
        return new BoundSafeUpdaterBuilderImpl<>(session.feature(TopicUpdateControl.class), valueType, transformer);
    }

    @Override
    public void register(
            TopicUpdateControl updateControl,
            String topicPath,
            SafeTransformedUpdateSource<S, T> updateSource) {
        updateControl.registerUpdateSource(
            topicPath,
            new SafeUpdateSourceAdapter<>(new UpdateControlValueCache(updateControl), this, updateSource));
    }
}
