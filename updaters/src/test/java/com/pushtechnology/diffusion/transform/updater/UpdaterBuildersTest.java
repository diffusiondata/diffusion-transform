package com.pushtechnology.diffusion.transform.updater;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Unit tests for {@link UpdaterBuilders}.
 *
 * @author Push Technology Limited
 */
public final class UpdaterBuildersTest {

    @Test
    public void safeBuilder() {
        final SafeUpdaterBuilder<JSON, JSON> jsonUpdater = UpdaterBuilders.updaterBuilder(JSON.class);

        assertTrue(jsonUpdater instanceof SafeUpdaterBuilderImpl);
    }

    @Test
    public void jsonSafeBuilder() {
        final SafeUpdaterBuilder<JSON, JSON> jsonUpdater = UpdaterBuilders.jsonUpdaterBuilder();

        assertTrue(jsonUpdater instanceof SafeUpdaterBuilderImpl);
    }

    @Test
    public void binarySafeBuilder() {
        final SafeUpdaterBuilder<Binary, Binary> binaryUpdater = UpdaterBuilders.binaryUpdaterBuilder();

        assertTrue(binaryUpdater instanceof SafeUpdaterBuilderImpl);
    }
}
