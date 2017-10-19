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

package com.pushtechnology.diffusion.transform.stream;

import java.util.function.Function;

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A stream adapter that uses a {@link Function} to convert the values.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
/*package*/ final class SafeStreamAdapter<S, T> extends AbstractStreamAdapter<S, T, Topics.ValueStream<T>> {
    private static final Logger LOG = LoggerFactory.getLogger(SafeStreamAdapter.class);
    private final Function<S, T> transformer;

    /**
     * Constructor.
     */
    /*package*/ SafeStreamAdapter(Function<S, T> transformer, Topics.ValueStream<T> delegate) {
        super(delegate);
        this.transformer = transformer;
    }

    @Override
    public void onValue(String topicPath, TopicSpecification topicSpecification, S oldValue, S newValue) {
        final T transformedNewValue;
        try {
            transformedNewValue = transformer.apply(newValue);
        }
        // CHECKSTYLE.OFF: IllegalCatch
        catch (RuntimeException e) {
            LOG.error("RuntimeException thrown by supposedly SafeTransformer.", e);
            return;
        }
        // CHECKSTYLE.ON: IllegalCatch

        final T transformedOldValue = storeInCache(topicPath, transformedNewValue);
        final Topics.ValueStream<T> delegate = getDelegate();
        try {
            delegate.onValue(topicPath, topicSpecification, transformedOldValue, transformedNewValue);
        }
        // CHECKSTYLE.OFF: IllegalCatch
        catch (RuntimeException e) {
            LOG.warn(
                "RuntimeException thrown by stream handler '{}' for topic '{}' with old value '{}' and new value '{}'",
                delegate,
                transformedOldValue,
                transformedNewValue,
                topicPath,
                e);
        }
        // CHECKSTYLE.ON: IllegalCatch
    }
}
