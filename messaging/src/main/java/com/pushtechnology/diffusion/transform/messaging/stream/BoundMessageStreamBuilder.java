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

package com.pushtechnology.diffusion.transform.messaging.stream;

import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A builder for {@link MessageStream}s that has been bound to a session.
 *
 * @param <V> the type of values
 * @param <S> the type of stream
 * @author Push Technology Limited
 */
public interface BoundMessageStreamBuilder<V, S extends MessageStream<V>> extends MessageStreamBuilder<V> {
    @Override
    <R> BoundTransformedMessageStreamBuilder<R> transform(Transformer<V, R> newTransformer);

    @Override
    <R> BoundTransformedMessageStreamBuilder<R> transformWith(UnsafeTransformer<V, R> newTransformer);

    /**
     * Register a message stream.
     * @param stream the stream to register
     */
    MessageStreamHandle register(S stream);
}
