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

package com.pushtechnology.diffusion.transform.messaging.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.session.SessionId;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * A handler of transformed values received as messages from sessions.
 *
 * @param <V> the type of the transformed values
 * @author Push Technology Limited
 */
public interface TransformedMessageHandler<V> extends MessageHandler<V> {

    /**
     * Notifies the failure to transform a value.
     *
     * @param path the path to which the message was sent
     * @param value the value that could not be transformed
     * @param sessionId the session that sent the message
     * @param sessionProperties the session properties of the session that sent the message
     * @param e the exception thrown when attempting to transform the value
     */
    void onTransformationException(
        String path,
        Content value,
        SessionId sessionId,
        Map<String, String> sessionProperties,
        TransformationException e);

    /**
     * Default implementation of a {@link TransformedMessageHandler}.
     *
     * @param <V> the type of the transformed values
     */
    class Default<V> extends MessageHandler.Default<V> implements TransformedMessageHandler<V> {
        private static final Logger LOG = LoggerFactory.getLogger(TransformedMessageHandler.Default.class);

        @Override
        public void onTransformationException(
                String path,
                Content value,
                SessionId sessionId,
                Map<String, String> sessionProperties,
                TransformationException e) {

            LOG.warn("{} transformation error, path={}, session={}, value={}", this, path, sessionId, value, e);
        }
    }
}
