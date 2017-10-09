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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;
import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link BoundSafeTopicAdderBuilder}.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
/*package*/ final class BoundSafeTopicAdderBuilderImpl<S extends Bytes, T>
    implements BoundSafeTopicAdderBuilder<S, T> {

    private final TopicType topicType;
    private final SafeTransformer<T, S> transformer;
    private final TopicControl topicControl;

    BoundSafeTopicAdderBuilderImpl(TopicType topicType, SafeTransformer<T, S> transformer, TopicControl topicControl) {
        this.topicType = topicType;
        this.transformer = transformer;
        this.topicControl = topicControl;
    }

    @Override
    public <R> BoundSafeTopicAdderBuilder<S, R> transform(SafeTransformer<R, T> newTransformer) {
        return new BoundSafeTopicAdderBuilderImpl<>(topicType, chain(newTransformer, transformer), topicControl);
    }

    @Override
    public <R> BoundSafeTopicAdderBuilder<S, R> transform(SafeTransformer<R, T> newTransformer, Class<R> type) {
        return new BoundSafeTopicAdderBuilderImpl<>(topicType, chain(newTransformer, transformer), topicControl);
    }

    @Override
    public <R> BoundTransformedTopicAdderBuilder<S, R> transform(Transformer<R, T> newTransformer) {
        return new BoundTransformedTopicAdderBuilderImpl<>(topicType, chain(newTransformer, transformer), topicControl);
    }

    @Override
    public <R> BoundTransformedTopicAdderBuilder<S, R> transform(Transformer<R, T> newTransformer, Class<R> type) {
        return new BoundTransformedTopicAdderBuilderImpl<>(topicType, chain(newTransformer, transformer), topicControl);
    }

    @Override
    public <R> BoundTransformedTopicAdderBuilder<S, R> transformWith(UnsafeTransformer<R, T> newTransformer) {
        return new BoundTransformedTopicAdderBuilderImpl<>(
            topicType,
            chain(toTransformer(newTransformer), transformer), topicControl);
    }

    @Override
    public <R> BoundTransformedTopicAdderBuilder<S, R> transformWith(
            UnsafeTransformer<R, T> newTransformer,
            Class<R> type) {
        return new BoundTransformedTopicAdderBuilderImpl<>(
            topicType,
            chain(toTransformer(newTransformer), transformer), topicControl);
    }

    @Override
    public SafeTopicAdder<T> create() {
        return new SafeTopicAdderImpl<>(topicControl, topicType, transformer);
    }
}
