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

import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * An extension to {@link UpdaterBuilder} that is bound to a session.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @param <U> The type of updater
 * @param <V> The type of update source
 * @author Push Technology Limited
 */
public interface BoundUpdaterBuilder<S, T, U extends TransformedUpdater<S, T>, V extends
        TransformedUpdateSource<S, T, U>> extends UpdaterBuilder<S, T> {

    @Override
    <R> BoundTransformedUpdaterBuilder<S, R> transform(Transformer<R, T> newTransformer);

    @Override
    <R> BoundTransformedUpdaterBuilder<S, R> transform(Transformer<R, T> newTransformer, Class<R> type);

    @Override
    <R> BoundTransformedUpdaterBuilder<S, R> transformWith(UnsafeTransformer<R, T> newTransformer);

    @Override
    <R> BoundTransformedUpdaterBuilder<S, R> transformWith(UnsafeTransformer<R, T> newTransformer, Class<R> type);

    /**
     * Create the updater.
     */
    U create();

    /**
     * Register an update source.
     * @param updateSource the update source
     */
    void register(String topicPath, V updateSource);

    /**
     * @return an unbound updater builder
     */
    UnboundUpdaterBuilder<S, T, U, V> unbind();
}
