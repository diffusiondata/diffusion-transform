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

import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * An extension to {@link UpdaterBuilder} that is not bound to a session.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @param <U> The type of updater
 * @param <V> The type of update source
 * @author Push Technology Limited
 */
public interface UnboundUpdaterBuilder
    <S, T, U extends TransformedUpdater<S, T>, V extends TransformedUpdateSource<S, T, U>>
    extends UpdaterBuilder<S, T> {

    @Override
    <R> UnboundTransformedUpdaterBuilder<S, R> transform(Transformer<R, T> newTransformer);

    @Override
    <R> UnboundTransformedUpdaterBuilder<S, R> transform(Transformer<R, T> newTransformer, Class<R> type);

    @Override
    <R> UnboundTransformedUpdaterBuilder<S, R> transformSafely(UnsafeTransformer<R, T> newTransformer);

    @Override
    <R> UnboundTransformedUpdaterBuilder<S, R> transformSafely(UnsafeTransformer<R, T> newTransformer, Class<R> type);

    /**
     * Bind an {@link UpdaterBuilder} to a session.
     * @param updateControl The feature of the session to bind
     * @return The bound updater
     */
    BoundUpdaterBuilder<S, T, U, V> bind(TopicUpdateControl updateControl);

    /**
     * Create the updater.
     *
     * @param updater the update to transform
     */
    U create(TopicUpdateControl.Updater updater);

    /**
     * Register an update source.
     * @param updateSource the update source
     */
    void register(TopicUpdateControl updateControl, String topicPath, V updateSource);
}
