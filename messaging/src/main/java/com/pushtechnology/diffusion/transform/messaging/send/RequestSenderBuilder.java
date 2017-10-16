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

import com.pushtechnology.diffusion.transform.transformer.Transformer;
import com.pushtechnology.diffusion.transform.transformer.UnsafeTransformer;

/**
 * A builder for {@link RequestToHandlerSender}s.
 *
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public interface RequestSenderBuilder<U, V> {
    /**
     * Transform the sender that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new sender builder
     */
    <R> RequestSenderBuilder<R, V> transformRequest(Transformer<R, U> newTransformer);

    /**
     * Transform the sender that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new sender builder
     */
    <R> RequestSenderBuilder<R, V> transformRequestWith(UnsafeTransformer<R, U> newTransformer);

    /**
     * Transform the sender that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new sender builder
     */
    <R> RequestSenderBuilder<U, R> transformResponse(Transformer<V, R> newTransformer);

    /**
     * Transform the sender that will be built.
     *
     * @param newTransformer the new transformer
     * @param <R> the new type of the transformed values
     * @return a new sender builder
     */
    <R> RequestSenderBuilder<U, R> transformResponseWith(UnsafeTransformer<V, R> newTransformer);
}
