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

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A {@link StreamBuilder} that creates a transformed stream.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
/*package*/ final class StreamBuilderImpl<S, T> extends AbstractStreamBuilder<S, T, TransformedStream<S, T>> {
    private final Transformer<S, T> transformer;

    /**
     * Constructor.
     */
    /*package*/ StreamBuilderImpl(Class<S> valueType, Transformer<S, T> transformer) {
        super(valueType);
        this.transformer = transformer;
    }

    @Override
    public <R> StreamBuilder<S, R, TransformedStream<S, R>> transform(Transformer<T, R> newTransformer) {
        return new StreamBuilderImpl<>(valueType, chain(transformer, newTransformer));
    }

    @Override
    public <R> StreamBuilder<S, R, TransformedStream<S, R>> transformWith(UnsafeTransformer<T, R> newTransformer) {
        return new StreamBuilderImpl<>(valueType, chain(transformer, toTransformer(newTransformer)));
    }

    @Override
    protected Topics.ValueStream<S> adaptStream(TransformedStream<S, T> targetStream) {
        return new StreamAdapter<>(transformer, targetStream);
    }
}
