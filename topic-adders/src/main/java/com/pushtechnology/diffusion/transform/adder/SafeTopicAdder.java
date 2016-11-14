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

package com.pushtechnology.diffusion.transform.adder;

import com.pushtechnology.diffusion.client.features.control.topics.TopicControl;

/**
 * Topic adder. Supports creating topics.
 *
 * @param <V> The type of value used to initialise the topic
 * @author Push Technology Limited
 */
public interface SafeTopicAdder<V> {
    /**
     * Add a new topic with an initial value.
     * @param topicPath the full path of the topic to be created
     * @param initialValue the initial value
     * @param callback called with the result
     */
    void add(String topicPath, V initialValue, TopicControl.AddCallback callback);

    /**
     * Add a new topic without any value.
     * @param topicPath the full path of the topic to be created
     * @param callback called with the result
     */
    void add(String topicPath, TopicControl.AddCallback callback);
}
