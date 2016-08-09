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

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * A stream of transformed values. Provides the same notifications as a {@link Topics.ValueStream} but add an additional
 * one for handling exceptions thrown when transforming or binding the values.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
public interface TransformedStream<S, T> extends Topics.ValueStream<T> {
    /**
     * Notifies the failure to transform a value.
     *
     * @param topicPath the topic path
     * @param specification the topic specification
     * @param value the value that could not be transformed
     * @param e the exception thrown when attempting to transform the value
     */
    void onTransformationException(
        String topicPath,
        TopicSpecification specification,
        S value,
        TransformationException e);

    /**
     * Default implementation of a {@link TransformedStream}.
     *
     * @param <S> the type of the source values
     * @param <T> the type of the transformed values
     */
    class Default<S, T> extends Topics.ValueStream.Default<T> implements TransformedStream<S, T> {
        private static final Logger LOG = getLogger(TransformedStream.Default.class);
        @Override
        public void onTransformationException(
                String topicPath,
                TopicSpecification specification,
                S value,
                TransformationException e) {
            LOG.warn("{} transformation error, topic={}, value={}", this, topicPath, value, e);
        }
    }
}
