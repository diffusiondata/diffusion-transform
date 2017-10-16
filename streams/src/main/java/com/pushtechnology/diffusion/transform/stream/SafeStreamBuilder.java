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

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;

/**
 * An immutable builder for streams.
 * <p>
 * This extends {@link StreamBuilder} with support for untransformed and safely transformed streams. If the
 * transformation does not need to throw an exception a {@link SafeTransformer} can be used. Chaining a
 * {@link SafeTransformer} to a {@link SafeStreamBuilder} returns another {@link SafeStreamBuilder}. The
 * {@link SafeStreamBuilder} can be used to register a stream that does not need to implement any exception handling.
 * <p>
 * The builder can be used to chain the {@link com.pushtechnology.diffusion.transform.transformer.Transformer}s that
 * will be applied to a stream. The builder can be used as a template to register multiple streams that apply the same
 * transformation. Once the transformations have been chained the {@link #register(Topics, String, Topics.ValueStream)}
 * method can be used to register the stream.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public interface SafeStreamBuilder<S, T> extends StreamBuilder<S, T, Topics.ValueStream<T>> {
    /**
     * Transform the stream that will be built.
     *
     * @param newTransformer the new safe transformer
     * @param <R> the new type of the transformed values
     * @return a new stream builder
     */
    <R> SafeStreamBuilder<S, R> transform(SafeTransformer<T, R> newTransformer);
}
