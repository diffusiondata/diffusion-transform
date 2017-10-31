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

import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * A sender of values as request. May transform them before sending. May
 * transform the response received.
 *
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
public interface RequestToHandlerSender<U, V> {

    /**
     * Send a request.
     *
     * @param path the path to send the message to
     * @param request the request
     *
     * @return the response
     * @throws TransformationException if the request cannot be transformed
     */
    CompletableFuture<V> sendRequest(String path, U request) throws TransformationException;
}
