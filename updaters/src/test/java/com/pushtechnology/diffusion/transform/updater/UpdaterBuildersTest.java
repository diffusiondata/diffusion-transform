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
