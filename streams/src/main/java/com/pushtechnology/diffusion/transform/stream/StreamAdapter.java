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

import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A transforming stream that uses {@link UnsafeTransformer} to convert the values.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
/*package*/ final class StreamAdapter<S, T> extends AbstractStreamAdapter<S, T, TransformedStream<S, T>> {
    private static final Logger LOG = LoggerFactory.getLogger(StreamAdapter.class);
    private final UnsafeTransformer<S, T> transformingFunction;

    /**
     * Constructor.
     */
    /*package*/ StreamAdapter(UnsafeTransformer<S, T> transformingFunction, TransformedStream<S, T> delegate) {
        super(delegate);
        this.transformingFunction = transformingFunction;
    }

    @Override
    public void onValue(String topicPath, TopicSpecification topicSpecification, S oldValue, S newValue) {
        final TransformedStream<S, T> delegate = getDelegate();
        final T transformedNewValue;
        try {
            transformedNewValue = transformingFunction.transform(newValue);
        }
        catch (TransformationException e) {
            delegate.onTransformationException(topicPath, topicSpecification, newValue, e);
            return;
        }
        // CHECKSTYLE.OFF: IllegalCatch
        catch (Exception e) {
            delegate.onTransformationException(topicPath, topicSpecification, newValue, new TransformationException(e));
            return;
        }
        // CHECKSTYLE.ON: IllegalCatch

        final T transformedOldValue = storeInCache(topicPath, transformedNewValue);
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
