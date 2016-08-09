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
 * An update source for transformed values. When it becomes active it provides a {@link TransformedUpdater} of
 * a correct type.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @param <U> The type of updater provided by the update source
 * @author Push Technology Limited
 */
public interface TransformedUpdateSource<S, T, U extends TransformedUpdater<S, T>> extends TopicTreeHandler {
    /**
     * State notification that this source is now active for the specified
     * topic path, and is therefore in a valid state to send updates on
     * topics at or below the registered topic path.
     *
     * @param topicPath the registration path
     *
     * @param updater an updater that may be used to update topics at or
     *        below the registered path.
     */
    void onActive(String topicPath, U updater);

    /**
     * Notification that this source is not currently allowed to provide
     * topic updates for the specified topic path. This indicates that
     * another UpdateSource is currently active for the given topic path.
     * <P>
     * Server policy will dictate when this UpdateSource is set to be
     * active.
     * <P>
     * If this UpdateSource was previously in an {@code active} state, any
     * {@link TransformedUpdater updater} instances for this topic path
     * will no longer be valid for use.
     *
     * @param topicPath the registration path
     */
    void onStandby(String topicPath);

    /**
     * Default {@link TransformedUpdateSource} implementation.
     * <P>
     * This simply logs callbacks at debug level.
     *
     * @param <S> The type of value understood by the topic
     * @param <T> The type of value updates are provided as
     */
    class Default<S, T>
            extends TopicTreeHandler.Default
            implements TransformedUpdateSource<S, T, TransformedUpdater<S, T>> {

        private static final Logger LOG = getLogger(TransformedUpdateSource.Default.class);

        @Override
        public void onActive(String topicPath, TransformedUpdater<S, T> updater) {
            LOG.debug("{} - TransformedUpdateSource is active for {}", this, topicPath);
        }

        @Override
        public void onStandby(String topicPath) {
            LOG.debug("{} - TransformedUpdateSource is in standby for {}", this, topicPath);
        }
    }
}
