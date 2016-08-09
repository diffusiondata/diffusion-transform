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

package com.pushtechnology.diffusion.transform.transformer;

/**
 * Thrown when attempting to transform a value to a new value fails. It will generally be used to wrap another
 * exception.
 *
 * @author Push Technology Limited
 */
public final class TransformationException extends Exception {

    /**
     * Constructor.
     * @param message description of the failure
     */
    public TransformationException(String message) {
        super("A value could not be transformed. " + message);
    }

    /**
     * Constructor. Wraps another exception that is the root cause.
     * @param cause the wrapped exception
     */
    public TransformationException(Exception cause) {
        super("A value could not be transformed", cause);
    }
}
