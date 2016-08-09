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

package com.pushtechnology.diffusion.transform.updater;

import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl.Updater.UpdateCallback;
import com.pushtechnology.diffusion.client.features.control.topics.TopicUpdateControl.Updater.UpdateContextCallback;
import com.pushtechnology.diffusion.client.session.SessionClosedException;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * A value updater that transforms values to a type known to Diffusion. Only supports transforming one way so the
 * cached value cannot be retrieved. Attempting to update a topic may throw a {@link TransformationException}.
 *
 * @param <S> The type of value understood by the topic
 * @param <T> The type of value updates are provided as
 * @author Push Technology Limited
 */
public interface TransformedUpdater<S, T> {
    /**
     * Updates a topic to a specified value.
     * <P>
     * The updater will first transform the value to the type supported by the topic.
     * <P>
     * This method will cache the value understood by the topic so that subsequent calls
     * for the same topic will be able to use the current value to calculate
     * deltas of change to send to the server rather than having to send the
     * whole value.
     *
     * @param topicPath the topic path
     * @param value the new value for the topic
     * @param callback the callback for notifications on the update request
     *
     * @throws IllegalArgumentException if any arguments are null
     * @throws SessionClosedException if the session is closed
     * @throws TransformationException if the new value could not be transformed to one supported by the topic
     */
    void update(String topicPath, T value, UpdateCallback callback)
        throws IllegalArgumentException, SessionClosedException, TransformationException;

    /**
     * Updates a topic to a specified value.
     * <P>
     * The updater will first transform the value to the type supported by the topic.
     * <P>
     * This method will cache the value understood by the topic so that subsequent calls
     * for the same topic will be able to use the current value to calculate
     * deltas of change to send to the server rather than having to send the
     * whole value.
     *
     * @param topicPath the topic path
     * @param value the new value for the topic
     * @param context the context to supply to callback notifications. May
     *        be null
     * @param callback the callback for notifications on the update request
     *
     * @param <C> the type of the context value
     *
     * @throws IllegalArgumentException if any arguments are null
     * @throws SessionClosedException if the session is closed
     * @throws TransformationException if the new value could not be transformed to one supported by the topic
     */
    <C> void update(String topicPath, T value, C context, UpdateContextCallback<C> callback)
        throws IllegalArgumentException, SessionClosedException, TransformationException;

    /**
     * @return The cache of values used by the updater for generating deltas.
     */
    ValueCache<S> untransformedValueCache();
}
