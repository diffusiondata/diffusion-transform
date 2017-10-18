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

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.Messaging;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * Adapter from {@link Messaging.RequestStream} to {@link TransformedRequestStream}.
 *
 * @param <S> the type of request understood by Diffusion
 * @param <T> the type of response understood by Diffusion
 * @param <U> the type of request
 * @param <V> the type of response
 * @author Push Technology Limited
 */
/*package*/ final class TransformedRequestStreamAdapter<S, T, U, V> implements Messaging.RequestStream<S, T> {
    private final InternalTransformer<S, U> requestTransformer;
    private final InternalTransformer<V, T> responseTransformer;
    private final TransformedRequestStream<S, U, V> delegate;

    /*package*/ TransformedRequestStreamAdapter(
        InternalTransformer<S, U> requestTransformer,
        InternalTransformer<V, T> responseTransformer,
        TransformedRequestStream<S, U, V> delegate) {

        this.requestTransformer = requestTransformer;
        this.responseTransformer = responseTransformer;
        this.delegate = delegate;
    }

    @Override
    public void onRequest(String path, S request, Responder<T> responder) {
        final TransformedResponder transformedResponder = new TransformedResponder(responder);

        try {
            delegate.onRequest(path, requestTransformer.transform(request), transformedResponder);
        }
        catch (TransformationException e) {
            delegate.onTransformationException(path, request, transformedResponder, e);
        }
    }

    @Override
    public void onClose() {
        delegate.onClose();
    }

    @Override
    public void onError(ErrorReason errorReason) {
        delegate.onError(errorReason);
    }

    private final class TransformedResponder implements TransformedRequestStream.Responder<V> {
        private final Responder<T> responder;

        TransformedResponder(Responder<T> responder) {
            this.responder = responder;
        }

        @Override
        public void respond(V response) throws TransformationException {
            responder.respond(responseTransformer.transform(response));
        }

        @Override
        public void reject(String errorMessage) {
            responder.reject(errorMessage);
        }
    }
}
