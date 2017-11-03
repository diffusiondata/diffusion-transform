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

import java.util.concurrent.CompletableFuture;

import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * Implementation of {@link RequestToHandlerSender}.
 *
 * @param <S> the type of request understood by Diffusion
 * @param <T> the type of response understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
/*package*/ final class RequestToHandlerSenderImpl<S, T, U, V> implements RequestToHandlerSender<U, V> {
    private final Messaging messaging;
    private final Class<S> rawRequestType;
    private final Class<T> rawResponseType;
    private final InternalTransformer<U, S> requestTransformer;
    private final InternalTransformer<T, V> responseTransformer;

    /**
     * Constructor.
     */
    /*package*/ RequestToHandlerSenderImpl(
        Messaging messaging,
        Class<S> rawRequestType,
        Class<T> rawResponseType,
        InternalTransformer<U, S> requestTransformer,
        InternalTransformer<T, V> responseTransformer) {

        this.messaging = messaging;
        this.rawRequestType = rawRequestType;
        this.rawResponseType = rawResponseType;
        this.requestTransformer = requestTransformer;
        this.responseTransformer = responseTransformer;
    }

    @Override
    public CompletableFuture<V> sendRequest(String path, U request) throws TransformationException {
        return messaging
            .sendRequest(path, requestTransformer.transform(request), rawRequestType, rawResponseType)
            .thenCompose(response -> {
                try {
                    return CompletableFuture.completedFuture(responseTransformer.transform(response));
                }
                catch (TransformationException e) {
                    final CompletableFuture<V> future = new CompletableFuture<>();
                    future.completeExceptionally(e);
                    return future;
                }
            });
    }
}
