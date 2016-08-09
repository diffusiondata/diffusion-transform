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

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.transform.transformer.SafeTransformer;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * A {@link SafeStreamBuilder} that does create a transformed stream.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @author Push Technology Limited
 */
/*package*/ final class SafeStreamBuilderImpl<S, T>
        extends AbstractStreamBuilder<S, T, Topics.ValueStream<T>>
        implements SafeStreamBuilder<S, T> {
    private final SafeTransformer<S, T> transformer;

    /**
     * Constructor.
     */
    /*package*/ SafeStreamBuilderImpl(Class<S> valueType, SafeTransformer<S, T> transformer) {
        super(valueType);
        this.transformer = transformer;
    }

    @Override
    public <R> StreamBuilder<S, R, TransformedStream<S, R>> transform(Transformer<T, R> newTransformer) {
        return new StreamBuilderImpl<>(valueType, chain(transformer, newTransformer));
    }

    @Override
    public <R> SafeStreamBuilder<S, R> transform(SafeTransformer<T, R> newTransformer) {
        return new SafeStreamBuilderImpl<>(valueType, chain(transformer, newTransformer));
    }

    @Override
    protected Topics.ValueStream<S> adaptStream(Topics.ValueStream<T> targetStream) {
        return new SafeStreamAdapter<>(transformer, targetStream);
    }
}
