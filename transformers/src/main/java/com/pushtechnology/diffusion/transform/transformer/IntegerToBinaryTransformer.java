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
    public static Transformer<Integer, Binary> INSTANCE = new IntegerToBinaryTransformer();

    private IntegerToBinaryTransformer() {
        super(4);
    }

    @Override
    protected void serialiseValue(DataOutput dataOutput, Integer value) throws TransformationException, IOException {

        dataOutput.writeInt(value);
    }
}
