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

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Implementation of {@link RequestToSessionSender}.
 *
 * @param <S> the type of request understood by Diffusion
 * @param <T> the type of response understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
/*package*/ final class RequestToSessionSenderImpl<S, T, U, V> implements RequestToSessionSender<T, U, V> {
    private final Session session;
    private final Class<S> rawRequestType;
    private final Class<T> rawResponseType;
    private final Transformer<U, S> requestTransformer;
    private final Transformer<T, V> responseTransformer;

    /**
     * Constructor.
     */
    /*package*/ RequestToSessionSenderImpl(
        Session session,
        Class<S> rawRequestType,
        Class<T> rawResponseType,
        Transformer<U, S> requestTransformer,
        Transformer<T, V> responseTransformer) {

        this.session = session;
        this.rawRequestType = rawRequestType;
        this.rawResponseType = rawResponseType;
        this.requestTransformer = requestTransformer;
        this.responseTransformer = responseTransformer;
    }

    @Override
    public CompletableFuture<V> sendRequest(
            SessionId sessionId,
            String path,
            U request) throws TransformationException {

        return session
            .feature(MessagingControl.class)
            .sendRequest(sessionId, path, requestTransformer.transform(request), rawRequestType, rawResponseType)
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

    @Override
    public CompletableFuture<Integer> sendRequest(
            String sessionFilter,
            String path,
            U request,
            TransformedFilterCallback<T, V> callback) throws TransformationException {

        return session
            .feature(MessagingControl.class)
            .sendRequestToFilter(
                sessionFilter,
                path,
                requestTransformer.transform(request),
                rawRequestType,
                rawResponseType,
                new CallbackAdapter(callback));
    }

    private final class CallbackAdapter implements MessagingControl.FilteredRequestCallback<T> {
        private final TransformedFilterCallback<T, V> callback;

        private CallbackAdapter(TransformedFilterCallback<T, V> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(SessionId sessionId, T response) {
            try {
                callback.onResponse(sessionId, responseTransformer.transform(response));
            }
            catch (TransformationException e) {
                callback.onTransformationException(sessionId, response, e);
            }
        }

        @Override
        public void onResponseError(SessionId sessionId, Throwable throwable) {
            callback.onResponseError(sessionId, throwable);
        }

        @Override
        public void onClose() {
            callback.onClose();
        }

        @Override
        public void onError(ErrorReason errorReason) {
            callback.onError(errorReason);
        }
    }
}
