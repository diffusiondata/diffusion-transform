package com.pushtechnology.diffusion.transform.updater;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.pushtechnology.diffusion.client.features.TimeSeries;
import com.pushtechnology.diffusion.datatype.json.JSON;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit tests for {@link SafeTransformedTimeSeriesUpdater}.
 *
 * @author Matt Champion 15/02/2018
 */
public final class SafeTransformedTimeSeriesUpdaterTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private TimeSeries timeSeries;

    @Mock
    private Function<JSON, JSON> transformer;

    @Mock
    private JSON json;

    @Mock
    private TimeSeries.EventMetadata metadata;

    @Test
    public void append() throws Exception {
        when(timeSeries.append("path", JSON.class, json)).thenReturn(completedFuture(metadata));
        when(transformer.apply(json)).thenReturn(json);

        final SafeTransformedTimeSeriesUpdater<JSON, JSON> updater = new SafeTransformedTimeSeriesUpdater<>(
            timeSeries,
            JSON.class,
            transformer);

        final CompletableFuture<TimeSeries.EventMetadata> future = updater.append("path", json);

        assertEquals(metadata, future.get());
    }

    @Test
    public void edit() throws Exception {
        when(timeSeries.edit("path", 0L, JSON.class, json)).thenReturn(completedFuture(metadata));
        when(transformer.apply(json)).thenReturn(json);

        final SafeTransformedTimeSeriesUpdater<JSON, JSON> updater = new SafeTransformedTimeSeriesUpdater<>(
            timeSeries,
            JSON.class,
            transformer);

        final CompletableFuture<TimeSeries.EventMetadata> future = updater.edit("path", 0L, json);

        assertEquals(metadata, future.get());
    }
}
