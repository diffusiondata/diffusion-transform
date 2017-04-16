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

import static com.pushtechnology.diffusion.transform.transformer.JacksonContext.JACKSON_CONTEXT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;

/**
 * Unit tests for {@link JacksonContext}.
 *
 * @author Push Technology Limited
 */
public final class JacksonContextTest {
    private static final JSONDataType JSON_DATA_TYPE = Diffusion.dataTypes().json();

    @Test
    public void toObject() throws TransformationException {
        final JSON json = JSON_DATA_TYPE.fromJsonString("\"some pop culture reference\"");
        final String asString = JACKSON_CONTEXT.toObject(json, String.class);
        assertEquals(asString, "some pop culture reference");
    }

    @Test
    public void toBean() throws TransformationException {
        final JSON json = JSON_DATA_TYPE.fromJsonString("{\"name\": \"a name\", \"someNumber\": 7}");
        final TestBean asBean = JACKSON_CONTEXT.toObject(json, TestBean.class);
        assertEquals(asBean.getName(), "a name");
        assertEquals(asBean.getSomeNumber(), 7);
    }

    @Test
    public void toType() throws TransformationException {
        final JSON json = JSON_DATA_TYPE.fromJsonString("\"some pop culture reference\"");
        final String asString = JACKSON_CONTEXT.toType(json, new TypeReference<String>() {});
        assertEquals(asString, "some pop culture reference");
    }

    @Test
    public void toMap() throws TransformationException {
        final JSON json = JSON_DATA_TYPE.fromJsonString("{\"key\": \"value\"}");
        final Map<String, ?> asMap = JACKSON_CONTEXT.toMap(json);
        assertThat(asMap, new IsMapContaining<>(equalTo("key"), CoreMatchers.<Object>equalTo("value")));
    }

    @Test
    public void toMapOf() throws TransformationException {
        final JSON json = JSON_DATA_TYPE.fromJsonString("{\"key\": \"value\"}");
        final Map<String, String> asMap = JACKSON_CONTEXT.toMapOf(json, String.class);
        assertThat(asMap, new IsMapContaining<>(equalTo("key"), equalTo("value")));
    }

    @Test
    public void fromPojo() throws TransformationException {
        final TestBean bean = new TestBean();
        bean.setName("a name");
        bean.setSomeNumber(7);

        final JSON json = JACKSON_CONTEXT.fromPojo(bean);
        final Map<String, ?> asMap = JACKSON_CONTEXT.toMap(json);
        assertThat(asMap, new IsMapContaining<>(equalTo("name"), CoreMatchers.<Object>equalTo("a name")));
        assertThat(asMap, new IsMapContaining<>(equalTo("someNumber"), CoreMatchers.<Object>equalTo(7)));
    }

    @Test
    public void fromMap() throws TransformationException {
        final Map<String, String> sourceMap = new HashMap<>();
        sourceMap.put("key", "value");
        final JSON json = JACKSON_CONTEXT.fromMap(sourceMap);
        final Map<String, String> asMap = JACKSON_CONTEXT.toMapOf(json, String.class);
        assertThat(asMap, new IsMapContaining<>(equalTo("key"), equalTo("value")));
    }
}
