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

package com.pushtechnology.diffusion.transform.messaging.receive;

import static com.pushtechnology.diffusion.transform.messaging.receive.InternalTransformer.toTransformer;

import java.util.concurrent.CompletableFuture;

import com.pushtechnology.diffusion.client.callbacks.Registration;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * Implementation of {@link UnboundRequestReceiverBuilder}.
 *
 * @param <S> the type of request understood by Diffusion
 * @param <T> the type of response understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
/*package*/ final class UnboundRequestReceiverBuilderImpl<S, T, U, V>
        implements UnboundRequestReceiverBuilder<S, U, V> {
    private final Class<S> requestType;
    private final Class<T> responseType;
    private final InternalTransformer<S, U> requestTransformer;
    private final InternalTransformer<V, T> responseTransformer;

    /**
     * Constructor.
     */
    /*package*/ UnboundRequestReceiverBuilderImpl(
        Class<S> requestType,
        Class<T> responseType,
        InternalTransformer<S, U> requestTransformer,
        InternalTransformer<V, T> responseTransformer) {

        this.requestType = requestType;
        this.responseType = responseType;
        this.requestTransformer = requestTransformer;
        this.responseTransformer = responseTransformer;
    }

    @Override
    public <R> UnboundRequestReceiverBuilder<S, R, V> transformRequest(UnsafeTransformer<U, R> newTransformer) {
        return new UnboundRequestReceiverBuilderImpl<>(
            requestType,
            responseType,
            requestTransformer.chainUnsafe(newTransformer),
            responseTransformer);
    }

    @Override
    public <R> UnboundRequestReceiverBuilder<S, U, R> transformResponse(UnsafeTransformer<R, V> newTransformer) {
        return new UnboundRequestReceiverBuilderImpl<>(
            requestType,
            responseType,
            requestTransformer,
            toTransformer(newTransformer).chain(responseTransformer));
    }

    @Override
    public BoundRequestReceiverBuilder<S, U, V> bind(Session session) {
        return new BoundRequestReceiverBuilderImpl<>(
            session,
            requestType,
            responseType,
            requestTransformer,
            responseTransformer);
    }

    @Override
    public void setStream(Session session, String selector, TransformedRequestStream<S, U, V> stream) {
        final Messaging.RequestStream<S, T> adapter = new RequestStreamAdapter<>(
            requestTransformer,
            responseTransformer,
            stream);

        session.feature(Messaging.class).setRequestStream(selector, requestType, responseType, adapter);
    }

    @Override
    public CompletableFuture<Registration> addRequestHandler(
        Session session,
        String selector,
        TransformedRequestHandler<S, U, V> handler,
        String... properties) {
        return session
            .feature(MessagingControl.class)
            .addRequestHandler(
                selector,
                requestType,
                responseType,
                new RequestHandlerAdapter<>(requestTransformer, responseTransformer, handler),
                properties);
    }
}
