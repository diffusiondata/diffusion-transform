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

package com.pushtechnology.diffusion.transform.adder;

import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * An extension to {@link TopicAdderBuilder} that is bound to a session.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @param <U> The type of adder created by this builder
 * @author Push Technology Limited
 * @deprecated since 2.0.0
 */
@Deprecated
public interface BoundTopicAdderBuilder<S, T, U extends TopicAdder<T>> extends TopicAdderBuilder<S, T> {
    @Override
    <R> BoundTransformedTopicAdderBuilder<S, R> transform(Transformer<R, T> newTransformer);

    @Override
    <R> BoundTransformedTopicAdderBuilder<S, R> transform(Transformer<R, T> newTransformer, Class<R> type);

    @Override
    <R> BoundTransformedTopicAdderBuilder<S, R> transformWith(UnsafeTransformer<R, T> newTransformer);

    @Override
    <R> BoundTransformedTopicAdderBuilder<S, R> transformWith(UnsafeTransformer<R, T> newTransformer, Class<R> type);

    /**
     * Create the adder.
     */
    U create();
}
