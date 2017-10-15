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

import com.pushtechnology.diffusion.client.callbacks.Stream;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * A sender of values as request. May transform them before sending. May
 * transform the response received.
 *
 * @param <T> the type of response understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
public interface RequestToSessionSender<T, U, V> {

    /**
     * Send a request.
     *
     * @param sessionId the recipient of the request
     * @param path the path to send the request to
     * @param request the request
     *
     * @return the response
     */
    CompletableFuture<V> sendRequest(SessionId sessionId, String path, U request) throws TransformationException;

    /**
     * Send a request to many sessions.
     *
     * @param sessionFilter the recipient of the request
     * @param path the path to send the request to
     * @param request the request
     * @param callback the callback that receives the responses
     *
     * @return the number of requests sent
     * @throws TransformationException if the request cannot be transformed
     */
    CompletableFuture<Integer> sendRequest(
        String sessionFilter,
        String path,
        U request,
        TransformedFilterCallback<T, V> callback) throws TransformationException;

    /**
     * The callback for transformed responses.
     *
     * @param <R> the type of response understood by Diffusion
     * @param <S> the type of response
     */
    interface TransformedFilterCallback<R, S> extends Stream {
        /**
         * Called when a response has been received.
         *
         * @param sessionId sessionId of the session that sent the response
         * @param response transformed response object
         */
        void onResponse(SessionId sessionId, S response);

        /**
         * Called when a response from a session results in an error.
         *
         * @param sessionId sessionId of the session in error
         * @param t the throwable reason of the response error
         */
        void onResponseError(SessionId sessionId, Throwable t);

        /**
         * Called when a response has been received but cannot be transformed.
         *
         * @param sessionId sessionId of the session that sent the response
         * @param response untransformed response object
         * @param e the transformation exception
         */
        void onTransformationException(SessionId sessionId, R response, TransformationException e);
    }
}
