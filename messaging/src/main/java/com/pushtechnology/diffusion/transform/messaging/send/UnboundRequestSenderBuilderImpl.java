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
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link UnboundRequestSenderBuilder}.
 *
 * @param <S> the type of request understood by Diffusion
 * @param <T> the type of response understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
/*package*/ final class UnboundRequestSenderBuilderImpl<S, T, U, V> implements UnboundRequestSenderBuilder<U, V> {
    private final Class<S> rawRequestType;
    private final Class<T> rawResponseType;
    private final Transformer<U, S> requestTransformer;
    private final Transformer<T, V> responseTransformer;

    /**
     * Constructor.
     */
    /*package*/ UnboundRequestSenderBuilderImpl(
        Class<S> rawRequestType,
        Class<T> rawResponseType,
        Transformer<U, S> requestTransformer,
        Transformer<T, V> responseTransformer) {

        this.rawRequestType = rawRequestType;
        this.rawResponseType = rawResponseType;
        this.requestTransformer = requestTransformer;
        this.responseTransformer = responseTransformer;
    }

    @Override
    public <R> UnboundRequestSenderBuilder<R, V> transformRequest(Transformer<R, U> newTransformer) {
        return new UnboundRequestSenderBuilderImpl<>(
            rawRequestType,
            rawResponseType,
            chain(newTransformer, requestTransformer),
            responseTransformer);
    }

    @Override
    public <R> UnboundRequestSenderBuilder<R, V> transformRequestWith(UnsafeTransformer<R, U> newTransformer) {
        return new UnboundRequestSenderBuilderImpl<>(
            rawRequestType,
            rawResponseType,
            chain(toTransformer(newTransformer), requestTransformer),
            responseTransformer);
    }

    @Override
    public <R> UnboundRequestSenderBuilder<U, R> transformResponse(Transformer<V, R> newTransformer) {
        return new UnboundRequestSenderBuilderImpl<>(
            rawRequestType,
            rawResponseType,
            requestTransformer,
            chain(responseTransformer, newTransformer));
    }

    @Override
    public <R> UnboundRequestSenderBuilder<U, R> transformResponseWith(UnsafeTransformer<V, R> newTransformer) {
        return new UnboundRequestSenderBuilderImpl<>(
            rawRequestType,
            rawResponseType,
            requestTransformer,
            chain(responseTransformer, toTransformer(newTransformer)));
    }

    @Override
    public BoundRequestSenderBuilder<U, V> bind(Session session) {
        return new BoundRequestSenderBuilderImpl<>(
            session.feature(Messaging.class),
            rawRequestType,
            rawResponseType,
            requestTransformer,
            responseTransformer);
    }

    @Override
    public RequestToHandlerSender<U, V> buildToHandlerSender(Session session) {
        return new RequestToHandlerSenderImpl<>(
            session.feature(Messaging.class),
            rawRequestType,
            rawResponseType,
            requestTransformer,
            responseTransformer);
    }
}
