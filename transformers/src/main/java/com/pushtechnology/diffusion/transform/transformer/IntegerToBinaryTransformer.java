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

import java.io.DataOutput;
import java.io.IOException;

import com.pushtechnology.diffusion.datatype.binary.Binary;

/**
 * Transformer from {@link Integer} to {@link Binary}.
 *
 * @author Push Technology Limited
 */
/*package*/ final class IntegerToBinaryTransformer extends ToBinaryTransformer<Integer> {
    /**
     * Instance of {@link IntegerToBinaryTransformer}.
     */
    public static final Transformer<Integer, Binary> INSTANCE = new IntegerToBinaryTransformer();

    private IntegerToBinaryTransformer() {
        super(4);
    }

    @Override
    protected void serialiseValue(DataOutput dataOutput, Integer value) throws TransformationException, IOException {

        dataOutput.writeInt(value);
    }
}
