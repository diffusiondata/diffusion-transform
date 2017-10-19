package com.pushtechnology.diffusion.transform.transformer;

import static java.util.function.Function.identity;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link UnsafeTransformer}.
 *
 * @author Matt Champion 19/10/2017
 */
public final class UnsafeTransformerTest {
    @Test
    public void chainFunction() throws Exception {
        assertEquals("apply", ((UnsafeTransformer<String, String>) value -> value)
            .chain(identity()).transform("apply"));
    }

    @Test
    public void chainTransformer() throws Exception {
        assertEquals("apply", ((UnsafeTransformer<String, String>) value -> value)
            .chainUnsafe(value -> value).transform("apply"));
    }
}
