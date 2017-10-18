package com.pushtechnology.diffusion.transform.messaging.receive;

import static org.junit.Assert.assertNotNull;

import com.pushtechnology.diffusion.datatype.json.JSON;

import org.junit.Test;

/**
 * Unit tests for {@link RequestReceiverBuilders}.
 *
 * @author Push Technology Limited
 */
public final class RequestReceiverBuildersTest {

    @Test
    public void requestStreamBuilder() {
        final UnboundRequestReceiverBuilder<JSON, JSON, JSON> builder = RequestReceiverBuilders
            .requestStreamBuilder(JSON.class, JSON.class);

        assertNotNull(builder);
    }
}
