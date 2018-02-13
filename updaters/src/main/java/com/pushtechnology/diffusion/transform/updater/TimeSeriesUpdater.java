/*******************************************************************************
 * Copyright (C) 2018 Push Technology Ltd.
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

import java.util.concurrent.CompletableFuture;

import com.pushtechnology.diffusion.client.features.TimeSeries.EventMetadata;

/**
 * Updater for time series topics.
 *
 * @param <V> The type of value updates are provided as
 * @author Push Technology Limited
 */
public interface TimeSeriesUpdater<V> {

    /**
     * Append an event to a time series topic.
     *
     * @param path the path to update
     * @param value the value to append
     * @return future resolving to metadata
     */
    CompletableFuture<EventMetadata> append(String path, V value);

    /**
     * Edit an event of a time series topic.
     *
     * @param path the path to update
     * @param value the value to append
     * @return future resolving to metadata
     */
    CompletableFuture<EventMetadata> edit(String path, long originalSequence, V value);
}
