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
import com.pushtechnology.diffusion.client.features.control.topics.TopicControl.AddCallback;
import com.pushtechnology.diffusion.client.topics.details.TopicType;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformer;

/**
 * Implementation of {@link TopicAdder}.
 *
 * @param <V> The type of value used to initialise the topic
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
/*package*/ final class TopicAdderImpl<V> implements TopicAdder<V> {
    private final TopicControl topicControl;
    private final TopicType topicType;
    private final Transformer<V, ? extends Bytes> transformer;

    TopicAdderImpl(TopicControl topicControl, TopicType topicType, Transformer<V, ? extends Bytes> transformer) {
        this.topicControl = topicControl;
        this.topicType = topicType;
        this.transformer = transformer;
    }

    @Override
    public void add(String topicPath, V initialValue, AddCallback callback) throws TransformationException {
        topicControl.addTopic(topicPath, topicType, transformer.transform(initialValue), callback);
    }

    @Override
    public void add(String topicPath, AddCallback callback) {
        topicControl.addTopic(topicPath, topicType, callback);
    }
}
