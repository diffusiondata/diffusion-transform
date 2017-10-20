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
import com.pushtechnology.diffusion.client.topics.TopicSelector;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * An immutable builder for streams. The builder can be used to chain the {@link Transformer}s that will be applied to
 * a stream. The builder can be used as a template to register multiple streams that apply the same transformation. Once
 * the transformations have been chained the {@link #register(Topics, String, Topics.ValueStream)} method can be used to
 * register the stream.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @param <V> the type of the value steam to be built
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public interface StreamBuilder<S, T, V extends Topics.ValueStream<T>> {
    /**
     * Transform the stream that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new stream builder
     * @deprecated since 2.0.0 in favour of methods using {@link UnsafeTransformer}
     */
    @Deprecated
    <R> StreamBuilder<S, R, TransformedStream<S, R>> transform(Transformer<T, R> newTransformer);

    /**
     * Transform the stream that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new stream builder
     */
    <R> StreamBuilder<S, R, TransformedStream<S, R>> transformWith(UnsafeTransformer<T, R> newTransformer);

    /**
     * Transform the stream that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new stream builder
     */
    <R> StreamBuilder<S, R, ? extends Topics.ValueStream<R>> transform(Function<T, R> newTransformer);

    /**
     * Create the stream.
     *
     * @param topicsFeature the topics feature
     * @param topicSelector the topic selector to match the stream
     * @param stream the stream handler
     * @return a handle to the stream
     */
    StreamHandle register(Topics topicsFeature, String topicSelector, V stream);

    /**
     * Create the stream.
     *
     * @param topicsFeature the topics feature
     * @param topicSelector the topic selector to match the stream
     * @param stream the stream handler
     * @return a handle to the stream
     */
    StreamHandle register(Topics topicsFeature, TopicSelector topicSelector, V stream);

    /**
     * Create the fallback stream.
     *
     * @param topicsFeature the topics feature
     * @param stream the stream handler
     * @return a handle to the stream
     */
    StreamHandle createFallback(Topics topicsFeature, V stream);
}
