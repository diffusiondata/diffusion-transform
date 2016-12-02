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

package com.pushtechnology.diffusion.transform.messaging;

import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;

/**
 * A stream of transformed values received as messages.
 *
 * @param <V> the type of the transformed values
 * @author Push Technology Limited
 */
public interface TransformedMessageStream<V> extends MessageStream<V> {

    /**
     * Notifies the failure to transform a value.
     *
     * @param path the path to which the message was sent
     * @param value the value that could not be transformed
     * @param e the exception thrown when attempting to transform the value
     */
    void onTransformationException(
        String path,
        Content value,
        TransformationException e);
}
