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
import java.nio.charset.Charset;

/**
 * Transformer from {@link String} to {@link com.pushtechnology.diffusion.datatype.binary.Binary}.
 *
 * @author Push Technology Limited
 */
/*package*/ final class StringToBinaryTransformer extends ToBinaryTransformer<String> {
    private final  Charset charset;

    /**
     * Constructor.
     */
    StringToBinaryTransformer(Charset charset) {
        super(64); // A string could be any size so the estimated size makes little difference
        this.charset = charset;
    }

    @Override
    protected void serialiseValue(DataOutput dataOutput, String value) throws TransformationException, IOException {
        dataOutput.write(value.getBytes(charset));
    }
}
