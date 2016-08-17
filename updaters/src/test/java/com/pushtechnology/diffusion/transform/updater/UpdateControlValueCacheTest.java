package com.pushtechnology.diffusion.transform.updater;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.topics.TopicSelector;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Unit tests for {@link UpdateControlValueCache}.
 *
 * @author Push Technology Limited
 */
public final class UpdateControlValueCacheTest {
    @Mock
    private TopicUpdateControl updateControl;
    @Mock
    private JSON jsonValue;

    private ValueCache<JSON> valueCache;

    @Before
    public void setUp() {
        initMocks(this);

        valueCache = new UpdateControlValueCache<>(updateControl);

        when(updateControl.getCachedValue("topic")).thenReturn(jsonValue);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(jsonValue, updateControl);
    }

    @Test
    public void getCachedValue() {
        final JSON cachedValue = valueCache.getCachedValue("topic");

        verify(updateControl).getCachedValue("topic");

        assertEquals(jsonValue, cachedValue);
    }

    @Test
    public void removeCachedValues() {
        valueCache.removeCachedValues("topic");

        verify(updateControl).removeCachedValues("topic");
    }

    @Test
    public void removeCachedValuesBySelector() {
        final TopicSelector selector = Diffusion.topicSelectors().parse("topic");

        valueCache.removeCachedValues(selector);

        verify(updateControl).removeCachedValues(selector);
    }
}
