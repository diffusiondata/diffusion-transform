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

import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;

/**
 * An extension to {@link TopicAdderBuilder} that creates {@link SafeTopicAdder}s.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
public interface SafeTopicAdderBuilder<S, T> extends TopicAdderBuilder<S, T> {
    /**
     * Transform the adder that will be built.
     *
     * @param newTransformer the new safe transformer
     * @param <R> the new type of the transformed values
     * @return a new adder builder
     */
    <R> SafeTopicAdderBuilder<S, R> transform(SafeTransformer<R, T> newTransformer);

    /**
     * Transform the adder that will be built.
     *
     * @param newTransformer the new safe transformer
     * @param <R> the new type of the transformed values
     * @return a new adder builder
     */
    <R> SafeTopicAdderBuilder<S, R> transform(SafeTransformer<R, T> newTransformer, Class<R> type);
}
