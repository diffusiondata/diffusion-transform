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

import com.pushtechnology.diffusion.client.callbacks.Stream;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * A transformed request stream.
 *
 * @param <T> the type of request understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
public interface TransformedRequestStream<T, U, V> extends Stream {

    /**
     * Notifies the stream of a transformed request.
     *
     * @param path the path
     * @param request the request that could not be transformed
     * @param responder a responder for the request
     */
    void onRequest(String path, U request, Responder<V> responder);

    /**
     * Notifies the failure to transform a value.
     *
     * @param path the path
     * @param request the request that could not be transformed
     * @param responder a responder for the request
     * @param e the exception thrown when attempting to transform the value
     */
    void onTransformationException(
        String path,
        T request,
        Responder<V> responder,
        TransformationException e);

    /**
     * Transformed responder.
     *
     * @param <R> response type
     */
    interface Responder<R> {
        /**
         * Respond to a request.
         *
         * @param response the response
         * @throws TransformationException if the response could not be transformed
         */
        void respond(R response) throws TransformationException;

        /**
         * Reject a request.
         *
         * @param errorMessage the error message
         */
        void reject(String errorMessage);
    }
}
