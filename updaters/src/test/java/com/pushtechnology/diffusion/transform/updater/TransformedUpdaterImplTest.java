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

import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Unit tests for {@link TransformedUpdaterImpl}.
 *
 * @author Push Technology Limited
 */
public final class TransformedUpdaterImplTest {
    @Mock
    private TopicUpdateControl.ValueUpdater<JSON> delegateUpdater;
    @Mock
    private JSON jsonValue;
    @Mock
    private Transformer<String, JSON> transformer;
    @Mock
    private TopicUpdateControl.Updater.UpdateCallback callback;
    @Mock
    private TopicUpdateControl.Updater.UpdateContextCallback contextCallback;

    private TransformedUpdaterImpl<JSON, String> updater;

    @Before
    public void setUp() throws TransformationException {
        initMocks(this);

        when(delegateUpdater.getCachedValue("topic")).thenReturn(jsonValue);
        when(transformer.transform("stringValue")).thenReturn(jsonValue);

        updater = new TransformedUpdaterImpl<>(delegateUpdater, transformer);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(callback, transformer, jsonValue, delegateUpdater);
    }

    @Test
    public void update() throws TransformationException {
        updater.update("topic", "stringValue", callback);

        verify(transformer).transform("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void updateWithContext() throws TransformationException {
        updater.update("topic", "stringValue", "context", contextCallback);

        verify(transformer).transform("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, "context", contextCallback);
    }

    @Test
    public void untransformedValueCache() {
        final ValueCache<JSON> jsonValueCache = updater.untransformedValueCache();

        final JSON cachedValue = jsonValueCache.getCachedValue("topic");

        assertEquals(jsonValue, cachedValue);
        verify(delegateUpdater).getCachedValue("topic");
    }
}
