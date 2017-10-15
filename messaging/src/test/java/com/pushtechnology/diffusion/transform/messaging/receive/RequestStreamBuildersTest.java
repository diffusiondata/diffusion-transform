package com.pushtechnology.diffusion.transform.messaging.receive;

import static org.junit.Assert.assertNotNull;

import com.pushtechnology.diffusion.datatype.json.JSON;

import org.junit.Test;

/**
 * Unit tests for {@link RequestStreamBuilders}.
 *
 * @author Push Technology Limited
 */
public final class RequestStreamBuildersTest {

    @Test
    public void requestStreamBuilder() {
        final UnboundRequestStreamBuilder<JSON, JSON, JSON> builder = RequestStreamBuilders
            .requestStreamBuilder(JSON.class, JSON.class);

        assertNotNull(builder);
    }
}
