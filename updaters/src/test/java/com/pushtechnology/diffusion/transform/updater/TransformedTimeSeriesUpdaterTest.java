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

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import com.pushtechnology.diffusion.client.features.TimeSeries;
import com.pushtechnology.diffusion.client.features.TimeSeries.EventMetadata;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit tests for {@link TransformedTimeSeriesUpdater}.
 *
 * @author Push Technology Limited
 */
public final class TransformedTimeSeriesUpdaterTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private TimeSeries timeSeries;

    @Mock
    private UnsafeTransformer<JSON, JSON> transformer;

    @Mock
    private JSON json;

    @Mock
    private EventMetadata metadata;

    @Test
    public void append() throws Exception {
        when(timeSeries.append("path", JSON.class, json)).thenReturn(completedFuture(metadata));
        when(transformer.transform(json)).thenReturn(json);

        final TransformedTimeSeriesUpdater<JSON, JSON> updater = new TransformedTimeSeriesUpdater<>(
            timeSeries,
            JSON.class,
            transformer);

        final CompletableFuture<EventMetadata> future = updater.append("path", json);

        assertEquals(metadata, future.get());
    }

    @Test
    public void edit() throws Exception {
        when(timeSeries.edit("path", 0L, JSON.class, json)).thenReturn(completedFuture(metadata));
        when(transformer.transform(json)).thenReturn(json);

        final TransformedTimeSeriesUpdater<JSON, JSON> updater = new TransformedTimeSeriesUpdater<>(
            timeSeries,
            JSON.class,
            transformer);

        final CompletableFuture<EventMetadata> future = updater.edit("path", 0L, json);

        assertEquals(metadata, future.get());
    }

    @Test
    public void appendFail() throws Exception {
        when(timeSeries.append("path", JSON.class, json)).thenReturn(completedFuture(metadata));
        final Exception exception = new Exception("test");
        when(transformer.transform(json)).thenThrow(exception);

        final TransformedTimeSeriesUpdater<JSON, JSON> updater = new TransformedTimeSeriesUpdater<>(
            timeSeries,
            JSON.class,
            transformer);

        final CompletableFuture<EventMetadata> future = updater.append("path", json);

        expectedException.expectCause(equalTo(exception));
        assertEquals(metadata, future.get());
    }

    @Test
    public void editFail() throws Exception {
        when(timeSeries.edit("path", 0L, JSON.class, json)).thenReturn(completedFuture(metadata));
        final Exception exception = new Exception("test");
        when(transformer.transform(json)).thenThrow(exception);

        final TransformedTimeSeriesUpdater<JSON, JSON> updater = new TransformedTimeSeriesUpdater<>(
            timeSeries,
            JSON.class,
            transformer);

        final CompletableFuture<EventMetadata> future = updater.edit("path", 0L, json);

        expectedException.expectCause(equalTo(exception));
        future.get();
    }
}
