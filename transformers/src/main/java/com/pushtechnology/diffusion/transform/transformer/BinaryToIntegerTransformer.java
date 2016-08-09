package com.pushtechnology.diffusion.transform.transformer;

import java.io.DataInput;
import java.io.IOException;

import com.pushtechnology.diffusion.datatype.binary.Binary;

/**
 * Transformer from {@link Binary} to {@link Integer}.
 *
 * @author Push Technology Limited
 */
/*package*/ final class BinaryToIntegerTransformer extends FromBinaryTransformer<Integer> {
    public static Transformer<Binary, Integer> INSTANCE = new BinaryToIntegerTransformer();

    private BinaryToIntegerTransformer() {
    }

    @Override
    protected Integer deserialiseValue(DataInput dataInput) throws TransformationException, IOException {

        return dataInput.readInt();
    }
}
