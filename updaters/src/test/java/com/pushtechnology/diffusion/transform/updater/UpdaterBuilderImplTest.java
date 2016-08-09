package com.pushtechnology.diffusion.transform.updater;

import static com.pushtechnology.diffusion.transform.transformer.Transformers.identity;
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
 * Unit tests for {@link UpdaterBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class UpdaterBuilderImplTest {
    @Mock
    private TopicUpdateControl.Updater simpleUpdater;
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

    private UpdaterBuilderImpl<JSON, JSON> updaterBuilder;

    @Before
    public void setUp() throws TransformationException {
        initMocks(this);

        when(delegateUpdater.getCachedValue("topic")).thenReturn(jsonValue);
        when(transformer.transform("stringValue")).thenReturn(jsonValue);
        when(simpleUpdater.valueUpdater(JSON.class)).thenReturn(delegateUpdater);

        updaterBuilder = new UpdaterBuilderImpl<>(JSON.class, identity(JSON.class));
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(callback, transformer, jsonValue, delegateUpdater);
    }

    @Test
    public void createAndUpdate() throws TransformationException {
        final TransformedUpdater<JSON, JSON> updater = updaterBuilder.create(simpleUpdater);

        updater.update("topic", jsonValue, callback);

        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void createWithClassAndUpdate() throws TransformationException {
        final TransformedUpdater<JSON, JSON> updater = updaterBuilder.create(simpleUpdater, JSON.class);

        updater.update("topic", jsonValue, callback);

        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void untransformedValueCache() {
        final TransformedUpdater<JSON, JSON> updater = updaterBuilder.create(simpleUpdater);

        final ValueCache<JSON> jsonValueCache = updater.untransformedValueCache();

        final JSON cachedValue = jsonValueCache.getCachedValue("topic");

        assertEquals(jsonValue, cachedValue);
        verify(delegateUpdater).getCachedValue("topic");
    }

    @Test
    public void transformCreateAndUpdate() throws TransformationException {
        final TransformedUpdater<JSON, String> updater = updaterBuilder
            .transform(transformer)
            .create(simpleUpdater);

        updater.update("topic", "stringValue", callback);

        verify(transformer).transform("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void transformCreateAndUpdateWithClass() throws TransformationException {
        final TransformedUpdater<JSON, String> updater = updaterBuilder
            .transform(transformer, String.class)
            .create(simpleUpdater);

        updater.update("topic", "stringValue", "context", contextCallback);

        verify(transformer).transform("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, "context", contextCallback);
    }
}
