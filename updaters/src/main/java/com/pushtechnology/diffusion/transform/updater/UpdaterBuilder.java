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

import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Builder for {@link TransformedUpdater}s.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
public interface UpdaterBuilder<S, T> {

    /**
     * Transform the updater that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new updater builder
     */
    <R> TransformedUpdaterBuilder<S, R> unsafeTransform(UnsafeTransformer<R, T> newTransformer);

    /**
     * Transform the updater that will be built.
     *
     * @param newTransformer the new transformer
     * @param type the new class of the transformed values
     * @param <R> the new type of the transformed values
     * @return a new updater builder
     */
    <R> TransformedUpdaterBuilder<S, R> unsafeTransform(UnsafeTransformer<R, T> newTransformer, Class<R> type);
}
