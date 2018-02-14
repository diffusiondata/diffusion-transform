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

import com.pushtechnology.diffusion.client.features.TimeSeries;
import com.pushtechnology.diffusion.client.features.TimeSeries.EventMetadata;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Transformed updater for time series topics.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
/*package*/ final class TransformedTimeSeriesUpdater<T, S> implements TimeSeriesUpdater<T> {
    private final TimeSeries timeSeries;
    private final Class<S> valueClass;
    private final UnsafeTransformer<T, S> transformer;

    /*package*/ TransformedTimeSeriesUpdater(
            TimeSeries timeSeries,
            Class<S> valueClass,
            UnsafeTransformer<T, S> transformer) {
        this.timeSeries = timeSeries;
        this.valueClass = valueClass;
        this.transformer = transformer;
    }

    @Override
    public CompletableFuture<EventMetadata> append(String path, T value) {
        try {
            return timeSeries.append(path, valueClass, transformer.transform(value));
        }
        // CHECKSTYLE.OFF: IllegalCatch
        catch (Exception e) {
            final CompletableFuture<EventMetadata> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
        // CHECKSTYLE.ON: IllegalCatch
    }

    @Override
    public CompletableFuture<EventMetadata> edit(String path, long originalSequence, T value) {
        try {
            return timeSeries.edit(path, originalSequence, valueClass, transformer.transform(value));
        }
        // CHECKSTYLE.OFF: IllegalCatch
        catch (Exception e) {
            final CompletableFuture<EventMetadata> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
        // CHECKSTYLE.ON: IllegalCatch
    }
}
