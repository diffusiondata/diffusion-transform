
package com.pushtechnology.diffusion.transform.transformer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pushtechnology.diffusion.datatype.binary.Binary;

/**
 * Abstract {@link Transformer} to help deserialise values from {@link Binary}.
 *
 * @author Push Technology Limited
 */
public abstract class FromBinaryTransformer<T> implements Transformer<Binary, T> {
    @Override
    public T transform(Binary value) throws TransformationException {
        final InputStream inputStream = value.asInputStream();
        final DataInputStream dataInputStream = new DataInputStream(inputStream);

        try {
            return deserialiseValue(dataInputStream);
        }
        catch (IOException e) {
            throw new TransformationException(e);
        }
    }

    /**
     * Deserialise the value.
     *
     * @param dataInput input to read from
     * @throws TransformationException if the value cannot be deserialised
     * @throws IOException if the operations on the input failed
     */
    protected abstract T deserialiseValue(DataInput dataInput) throws TransformationException, IOException;
}
