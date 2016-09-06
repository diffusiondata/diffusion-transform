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

import com.pushtechnology.diffusion.client.callbacks.Stream;
import com.pushtechnology.diffusion.client.features.Topics;

/**
 * Implementation of {@link StreamHandle}.
 *
 * @author Push Technology Limited
 */
/*package*/ final class StreamHandleImpl implements StreamHandle {
    private final Topics topicsFeature;
    private final Stream stream;

    StreamHandleImpl(Topics topicsFeature, Stream stream) {
        this.topicsFeature = topicsFeature;
        this.stream = stream;
    }

    @Override
    public void close() {
        topicsFeature.removeStream(stream);
    }
}
