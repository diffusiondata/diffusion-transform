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

import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Implementation of {@link SafeUpdaterBuilder}.

 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
/*package*/ final class SafeUpdaterBuilderImpl<S, T> implements SafeUpdaterBuilder<S, T> {
    private final Class<S> valueType;
    private final SafeTransformer<T, S> transformer;

    SafeUpdaterBuilderImpl(Class<S> valueType, SafeTransformer<T, S> transformer) {
        this.valueType = valueType;
        this.transformer = transformer;
    }

    @Override
    public <R> TransformedUpdaterBuilder<S, R> transform(Transformer<R, T> newTransformer) {
        return new UpdaterBuilderImpl<>(valueType, chain(newTransformer, transformer));
    }

    @Override
    public <R> TransformedUpdaterBuilder<S, R> transform(Transformer<R, T> newTransformer, Class<R> type) {
        return new UpdaterBuilderImpl<>(valueType, chain(newTransformer, transformer));
    }

    @Override
    public <R> SafeUpdaterBuilder<S, R> transform(SafeTransformer<R, T> newTransformer) {
        return new SafeUpdaterBuilderImpl<>(valueType, chain(newTransformer, transformer));
    }

    @Override
    public <R> SafeUpdaterBuilder<S, R> transform(SafeTransformer<R, T> newTransformer, Class<R> type) {
        return new SafeUpdaterBuilderImpl<>(valueType, chain(newTransformer, transformer));
    }

    @Override
    public SafeTransformedUpdater<S, T> create(TopicUpdateControl.Updater updater) {
        return new SafeTransformedUpdaterImpl<>(updater.valueUpdater(valueType), transformer);
    }

    @Override
    public void register(
            TopicUpdateControl updateControl,
            String topicPath,
            SafeTransformedUpdateSource<S, T> updateSource) {
        updateControl.registerUpdateSource(topicPath, new SafeUpdateSourceAdapter<>(this, updateSource));
    }
}
