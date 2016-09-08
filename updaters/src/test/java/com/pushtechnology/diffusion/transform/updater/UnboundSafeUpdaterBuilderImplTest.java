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
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Unit tests for {@link UnboundSafeUpdaterBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class UnboundSafeUpdaterBuilderImplTest {
    @Mock
    private TopicUpdateControl.Updater simpleUpdater;
    @Mock
    private TopicUpdateControl.ValueUpdater<JSON> delegateUpdater;
    @Mock
    private JSON jsonValue;
    @Mock
    private Transformer<String, JSON> transformer;
    @Mock
    private SafeTransformer<String, JSON> safeTransformer;
    @Mock
    private TopicUpdateControl.Updater.UpdateCallback callback;
    @Mock
    private TopicUpdateControl.Updater.UpdateContextCallback contextCallback;

    private UnboundSafeUpdaterBuilderImpl<JSON, JSON> updaterBuilder;

    @Before
    public void setUp() throws TransformationException {
        initMocks(this);

        when(delegateUpdater.getCachedValue("topic")).thenReturn(jsonValue);
        when(transformer.transform("stringValue")).thenReturn(jsonValue);
        when(safeTransformer.transform("stringValue")).thenReturn(jsonValue);
        when(simpleUpdater.valueUpdater(JSON.class)).thenReturn(delegateUpdater);

        updaterBuilder = new UnboundSafeUpdaterBuilderImpl<>(JSON.class, identity(JSON.class));
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(callback, transformer, safeTransformer, jsonValue, delegateUpdater);
    }

    @Test
    public void createAndUpdate() throws TransformationException {
        final SafeTransformedUpdater<JSON, JSON> updater = updaterBuilder.create(simpleUpdater);

        updater.update("topic", jsonValue, callback);

        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void untransformedValueCache() {
        final SafeTransformedUpdater<JSON, JSON> updater = updaterBuilder.create(simpleUpdater);

        final ValueCache<JSON> jsonValueCache = updater.untransformedValueCache();

        final JSON cachedValue = jsonValueCache.getCachedValue("topic");

        assertEquals(jsonValue, cachedValue);
        verify(delegateUpdater).getCachedValue("topic");
    }

    @Test
    public void safeTransformCreateAndUpdate() throws TransformationException {
        final SafeTransformedUpdater<JSON, String> updater = updaterBuilder
            .transform(safeTransformer)
            .create(simpleUpdater);

        updater.update("topic", "stringValue", callback);

        verify(safeTransformer).transform("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void safeTransformCreateAndUpdateWithClass() throws TransformationException {
        final SafeTransformedUpdater<JSON, String> updater = updaterBuilder
            .transform(safeTransformer, String.class)
            .create(simpleUpdater);

        updater.update("topic", "stringValue", "context", contextCallback);

        verify(safeTransformer).transform("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, "context", contextCallback);
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
