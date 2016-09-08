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

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.callbacks.Registration;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;

/**
 * An adapter from {@link TopicUpdateControl.UpdateSource} to {@link TransformedUpdateSource}.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
/*package*/ final class UpdateSourceAdapter<S, T>  implements TopicUpdateControl.UpdateSource {
    private final ValueCache valueCache;
    private final TransformedUpdateSource<S, T, TransformedUpdater<S, T>> updateSource;
    private final TransformedUpdaterBuilder<S, T> updaterBuilder;

    UpdateSourceAdapter(
            ValueCache valueCache,
            TransformedUpdaterBuilder<S, T> updaterBuilder,
            TransformedUpdateSource<S, T, TransformedUpdater<S, T>> updateSource) {
        this.valueCache = valueCache;
        this.updaterBuilder = updaterBuilder;
        this.updateSource = updateSource;
    }

    @Override
    public void onActive(String topicPath, TopicUpdateControl.Updater updater) {
        valueCache.removeCachedValues(topicPath);
        updateSource.onActive(topicPath, updaterBuilder.create(updater));
    }

    @Override
    public void onStandby(String topicPath) {
        updateSource.onStandby(topicPath);
    }

    @Override
    public void onRegistered(String topicPath, Registration registration) {
        updateSource.onRegistered(topicPath, registration);
    }

    @Override
    public void onClose(String topicPath) {
        updateSource.onClose(topicPath);
        valueCache.removeCachedValues(topicPath);
    }

    @Override
    public void onError(String topicPath, ErrorReason errorReason) {
        updateSource.onError(topicPath, errorReason);
        valueCache.removeCachedValues(topicPath);
    }
}
