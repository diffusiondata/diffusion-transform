/*******************************************************************************
 * Copyright (C) 2017 Push Technology Ltd.
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

package com.pushtechnology.diffusion.transform.messaging.send;

import static com.pushtechnology.diffusion.transform.transformer.Transformers.chain;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toTransformer;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link BoundRequestSenderBuilder}.
 *
 * @param <S> the type of request understood by Diffusion
 * @param <T> the type of response understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
/*package*/ final class BoundRequestSenderBuilderImpl<S, T, U, V> implements BoundRequestSenderBuilder<U, V> {
    private final Messaging messaging;
    private final Class<S> rawRequestType;
    private final Class<T> rawResponseType;
    private final Transformer<U, S> requestTransformer;
    private final Transformer<T, V> responseTransformer;

    /**
     * Constructor.
     */
    /*package*/ BoundRequestSenderBuilderImpl(
        Messaging messaging,
        Class<S> rawRequestType,
        Class<T> rawResponseType,
        Transformer<U, S> requestTransformer,
        Transformer<T, V> responseTransformer) {

        this.messaging = messaging;
        this.rawRequestType = rawRequestType;
        this.rawResponseType = rawResponseType;
        this.requestTransformer = requestTransformer;
        this.responseTransformer = responseTransformer;
    }

    @Override
    public <R> BoundRequestSenderBuilder<R, V> transformRequest(Transformer<R, U> newTransformer) {
        return new BoundRequestSenderBuilderImpl<>(
            messaging,
            rawRequestType,
            rawResponseType,
            chain(newTransformer, requestTransformer),
            responseTransformer);
    }

    @Override
    public <R> BoundRequestSenderBuilder<R, V> transformRequestWith(UnsafeTransformer<R, U> newTransformer) {
        return new BoundRequestSenderBuilderImpl<>(
            messaging,
            rawRequestType,
            rawResponseType,
            chain(toTransformer(newTransformer), requestTransformer),
            responseTransformer);
    }

    @Override
    public <R> BoundRequestSenderBuilder<U, R> transformResponse(Transformer<V, R> newTransformer) {
        return new BoundRequestSenderBuilderImpl<>(
            messaging,
            rawRequestType,
            rawResponseType,
            requestTransformer,
            chain(responseTransformer, newTransformer));
    }

    @Override
    public <R> BoundRequestSenderBuilder<U, R> transformResponseWith(UnsafeTransformer<V, R> newTransformer) {
        return new BoundRequestSenderBuilderImpl<>(
            messaging,
            rawRequestType,
            rawResponseType,
            requestTransformer,
            chain(responseTransformer, toTransformer(newTransformer)));
    }

    @Override
    public RequestToHandlerSender<U, V> buildToHandlerSender() {
        return new RequestToHandlerSenderImpl<>(
            messaging,
            rawRequestType,
            rawResponseType,
            requestTransformer,
            responseTransformer);
    }
}
