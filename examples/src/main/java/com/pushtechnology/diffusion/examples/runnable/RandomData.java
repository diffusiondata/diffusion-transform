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

package com.pushtechnology.diffusion.examples.runnable;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Value;

/**
 * Random data for examples.
 *
 * @author Push Technology Limited
 */
@Value
public class RandomData {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private static final Random RANDOM = new Random();

    int id;
    long timestamp;
    int randomInt;

    public static RandomData fromByteArray(byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        final int id = buffer.getInt();
        final long timestamp = buffer.getLong();
        final int randomInt = buffer.getInt();
        return new RandomData(id, timestamp, randomInt);
    }

    public static RandomData next() {
        return new RandomData(ID_GENERATOR.get(), System.currentTimeMillis(), RANDOM.nextInt());
    }
}
