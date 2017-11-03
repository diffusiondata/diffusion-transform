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
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.function.Function;

import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Unit tests for {@link UnboundSafeUpdaterBuilderImpl}.
 *
 * @author Push Technology Limited
 */
public final class UnboundSafeUpdaterBuilderImplTest {
    @Mock
    private Session session;
    @Mock
    private TopicUpdateControl updateControl;
    @Mock
    private SafeTransformedUpdateSource<JSON, String> updateSource;
    @Mock
    private TopicUpdateControl.Updater simpleUpdater;
    @Mock
    private TopicUpdateControl.ValueUpdater<JSON> delegateUpdater;
    @Mock
    private JSON jsonValue;
    @Mock
    private Function<String, JSON> safeTransformer;
    @Mock
    private UnsafeTransformer<String, JSON> unsafeTransformer;
    @Mock
    private TopicUpdateControl.Updater.UpdateCallback callback;
    @Captor
    private ArgumentCaptor<SafeTransformedUpdater<JSON, String>> updaterCaptor;
    @Captor
    private ArgumentCaptor<TopicUpdateControl.UpdateSource> updateSourceCaptor;

    private UnboundSafeUpdaterBuilderImpl<JSON, JSON> updaterBuilder;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(delegateUpdater.getCachedValue("topic")).thenReturn(jsonValue);
        when(safeTransformer.apply("stringValue")).thenReturn(jsonValue);
        when(unsafeTransformer.transform("stringValue")).thenReturn(jsonValue);
        when(unsafeTransformer.chainUnsafe(isA(UnsafeTransformer.class))).thenAnswer(new Answer<UnsafeTransformer>() {
            @Override
            public UnsafeTransformer answer(InvocationOnMock invocation) throws Throwable {
                return (Object value) -> invocation
                    .getArgumentAt(0, UnsafeTransformer.class)
                    .transform(unsafeTransformer.transform((String) value));
            }
        });
        when(unsafeTransformer.chain(isA(Function.class))).thenAnswer(new Answer<UnsafeTransformer>() {
            @Override
            public UnsafeTransformer answer(InvocationOnMock invocation) throws Throwable {
                return (Object value) -> invocation
                    .getArgumentAt(0, Function.class)
                    .apply(unsafeTransformer.transform((String) value));
            }
        });
        when(simpleUpdater.valueUpdater(JSON.class)).thenReturn(delegateUpdater);
        when(updateControl.updater()).thenReturn(simpleUpdater);
        when(session.feature(TopicUpdateControl.class)).thenReturn(updateControl);

        updaterBuilder = new UnboundSafeUpdaterBuilderImpl<>(JSON.class, identity(JSON.class));
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(
            callback,
            safeTransformer,
            jsonValue,
            delegateUpdater,
            unsafeTransformer,
            session);
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

        verify(safeTransformer).apply("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void transformWithCreateAndUpdate() throws Exception {
        final TransformedUpdater<JSON, String> updater = updaterBuilder
            .unsafeTransform(unsafeTransformer)
            .create(simpleUpdater);

        updater.update("topic", "stringValue", callback);

        verify(unsafeTransformer).chain(isA(Function.class));
        verify(unsafeTransformer).transform("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void transformCreateAndUpdate() throws Exception {
        final TransformedUpdater<JSON, String> updater = updaterBuilder
            .unsafeTransform(unsafeTransformer)
            .create(simpleUpdater);

        updater.update("topic", "stringValue", callback);

        verify(unsafeTransformer).chain(isA(Function.class));
        verify(unsafeTransformer).transform("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @Test
    public void transformRegisterAndUpdate() throws TransformationException {
        updaterBuilder
            .transform(safeTransformer)
            .register(updateControl, "topic", updateSource);

        verify(updateControl).registerUpdateSource(eq("topic"), updateSourceCaptor.capture());

        updateSourceCaptor.getValue().onActive("topic", simpleUpdater);

        verify(updateSource).onActive(eq("topic"), updaterCaptor.capture());

        updaterCaptor.getValue().update("topic", "stringValue", callback);

        verify(safeTransformer).apply("stringValue");
        verify(delegateUpdater).update("topic", jsonValue, callback);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void transformAndBindWithSession() throws TransformationException {
        final BoundSafeUpdaterBuilder<JSON, String> builder = updaterBuilder
            .transform(safeTransformer)
            .bind(session);

        builder.create();

        verify(session).feature(TopicUpdateControl.class);
        verify(updateControl).updater();
    }
}
