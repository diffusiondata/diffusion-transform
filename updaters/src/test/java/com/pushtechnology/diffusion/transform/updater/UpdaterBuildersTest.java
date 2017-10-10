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

package com.pushtechnology.diffusion.transform.updater;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.recordv2.RecordV2;

/**
 * Unit tests for {@link UpdaterBuilders}.
 *
 * @author Push Technology Limited
 */
public final class UpdaterBuildersTest {

    @Test
    public void safeBuilder() {
        final SafeUpdaterBuilder<JSON, JSON> updater = UpdaterBuilders.updaterBuilder(JSON.class);

        assertTrue(updater instanceof UnboundSafeUpdaterBuilderImpl);
    }

    @Test
    public void jsonSafeBuilder() {
        final SafeUpdaterBuilder<JSON, JSON> updater = UpdaterBuilders.jsonUpdaterBuilder();

        assertTrue(updater instanceof UnboundSafeUpdaterBuilderImpl);
    }

    @Test
    public void binarySafeBuilder() {
        final SafeUpdaterBuilder<Binary, Binary> updater = UpdaterBuilders.binaryUpdaterBuilder();

        assertTrue(updater  instanceof UnboundSafeUpdaterBuilderImpl);
    }

    @Test
    public void stringSafeBuilder() {
        final SafeUpdaterBuilder<String, String> updater = UpdaterBuilders.stringUpdaterBuilder();

        assertTrue(updater instanceof UnboundSafeUpdaterBuilderImpl);
    }

    @Test
    public void int64SafeBuilder() {
        final SafeUpdaterBuilder<Long, Long> updater = UpdaterBuilders.int64UpdaterBuilder();

        assertTrue(updater instanceof UnboundSafeUpdaterBuilderImpl);
    }

    @Test
    public void doubleSafeBuilder() {
        final SafeUpdaterBuilder<Double, Double> updater = UpdaterBuilders.doubleUpdaterBuilder();

        assertTrue(updater instanceof UnboundSafeUpdaterBuilderImpl);
    }

    @Test
    public void recordV2SafeBuilder() {
        final SafeUpdaterBuilder<RecordV2, RecordV2> updater = UpdaterBuilders.recordV2UpdaterBuilder();

        assertTrue(updater instanceof UnboundSafeUpdaterBuilderImpl);
    }
}
