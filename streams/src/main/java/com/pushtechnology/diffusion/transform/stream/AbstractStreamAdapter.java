/*******************************************************************************
 * Copyright (C) 2016 Push Technology Ltd.
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

package com.pushtechnology.diffusion.transform.stream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pushtechnology.diffusion.client.callbacks.ErrorReason;
import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;

/**
 * An abstract adapter for {@link Topics.ValueStream} to transformed value streams.
 *
 * @param <S> the type of the source values
 * @param <T> the type of the transformed values
 * @param <D> the type of the delegate value steam
 * @author Push Technology Limited
 */
/*package*/ abstract class AbstractStreamAdapter<S, T, D extends Topics.ValueStream<T>>
        implements Topics.ValueStream<S> {
    /**
     * Delegate value stream.
     */
    private final D delegate;
    /**
     * Cache of transformed values.
     */
    private final Map<String, T> valueCache = new ConcurrentHashMap<>();

    /**
     * Constructor.
     */
    protected AbstractStreamAdapter(D delegate) {
        this.delegate = delegate;
    }

    @Override
    public final void onSubscription(String topicPath, TopicSpecification specification) {
        delegate.onSubscription(topicPath, specification);
    }

    @Override
    public final void onUnsubscription(
            String topicPath,
            TopicSpecification topicSpecification,
            Topics.UnsubscribeReason reason) {
        delegate.onUnsubscription(topicPath, topicSpecification, reason);
        valueCache.remove(topicPath);
    }

    @Override
    public final void onClose() {
        delegate.onClose();
        valueCache.clear();
    }

    @Override
    public final void onError(ErrorReason errorReason) {
        if (ErrorReason.SESSION_CLOSED.equals(errorReason)) {
            delegate.onClose();
        }
        else {
            delegate.onError(errorReason);
        }
        valueCache.clear();
    }

    /**
     * @return the stream to delegate to
     */
    protected D getDelegate() {
        return delegate;
    }

    /**
     * Store a value in the cache. The cache is used to provide the previous value to the stream
     * @param topicPath the topic path
     * @param newValue the new value
     * @return the old value or null if it is the first value
     */
    protected T storeInCache(String topicPath, T newValue) {
        return valueCache.put(topicPath, newValue);
    }
}
