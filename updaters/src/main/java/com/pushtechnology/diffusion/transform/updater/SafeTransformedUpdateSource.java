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

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.pushtechnology.diffusion.client.callbacks.TopicTreeHandler;

/**
 * An extension to {@link TransformedUpdateSource} that provides a {@link SafeTransformedUpdater}.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
public interface SafeTransformedUpdateSource<S, T> extends TransformedUpdateSource<S, T, SafeTransformedUpdater<S, T>> {
    @Override
    void onActive(String topicPath, SafeTransformedUpdater<S, T> updater);

    /**
     * Default {@link SafeTransformedUpdateSource} implementation.
     * <P>
     * This simply logs callbacks at debug level.
     *
     * @param <S> The type of value understood by the topic
     * @param <T> The type of value updates are provided as
     */
    class Default<S, T>
            extends TopicTreeHandler.Default
            implements SafeTransformedUpdateSource<S, T> {

        private static final Logger LOG = getLogger(TransformedUpdateSource.Default.class);

        @Override
        public void onActive(String topicPath, SafeTransformedUpdater<S, T> updater) {
            LOG.debug("{} - SafeTransformedUpdateSource is active for {}", this, topicPath);
        }

        @Override
        public void onStandby(String topicPath) {
            LOG.debug("{} - SafeTransformedUpdateSource is in standby for {}", this, topicPath);
        }
    }
}
