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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.binary.BinaryDataType;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;

/**
 * Unit tests for {@link Transformers}.
 *
 * @author Push Technology Limited
 */
public final class TransformersTest {
    private static final JSONDataType JSON_DATA_TYPE = Diffusion.dataTypes().json();
    private static final BinaryDataType BINARY_DATA_TYPE = Diffusion.dataTypes().binary();

    @Test
    public void toObject() throws TransformationException {
        final JSON json = JSON_DATA_TYPE.fromJsonString("{\"name\": \"a name\", \"someNumber\": 7}");
        final Transformer<JSON, TestBean> transformer = Transformers.toObject(TestBean.class);
        final TestBean asBean = transformer.transform(json);
        assertEquals(asBean.getName(), "a name");
        assertEquals(asBean.getSomeNumber(), 7);
    }

    @Test
    public void toType() throws TransformationException {
        final JSON json = JSON_DATA_TYPE.fromJsonString("\"some pop culture reference\"");
        final Transformer<JSON, String> transformer = Transformers.toType(new TypeReference<String>() {});
        final String asString = transformer.transform(json);
        assertEquals(asString, "some pop culture reference");
    }

    @Test
    public void toMapOf() throws TransformationException {
        final JSON json = JSON_DATA_TYPE.fromJsonString("{\"key\": \"value\"}");
        final Transformer<JSON, Map<String, String>> transformer = Transformers.toMapOf(String.class);
        final Map<String, String> asMap = transformer.transform(json);
        assertThat(asMap, new IsMapContaining<>(equalTo("key"), equalTo("value")));
    }

    @Test
    public void fromPojo() throws TransformationException {
        final TestBean bean = new TestBean();
        bean.setName("a name");
        bean.setSomeNumber(7);

        final Transformer<TestBean, JSON> transformer = Transformers.fromPojo();
        final JSON json = transformer.transform(bean);
        final Map<String, ?> asMap = JacksonContext.toMap(json);
        assertThat(asMap, new IsMapContaining<>(equalTo("name"), CoreMatchers.<Object>equalTo("a name")));
        assertThat(asMap, new IsMapContaining<>(equalTo("someNumber"), CoreMatchers.<Object>equalTo(7)));
    }

    @Test
    public void fromMap() throws TransformationException {
        final Map<String, String> sourceMap = new HashMap<>();
        sourceMap.put("key", "value");
        final Transformer<Map<String, String>, JSON> transformer = Transformers.fromMap();
        final JSON json = transformer.transform(sourceMap);
        final Map<String, String> asMap = JacksonContext.toMapOf(json, String.class);
        assertThat(asMap, new IsMapContaining<>(equalTo("key"), equalTo("value")));
    }

    @Test
    public void identity() {
        final SafeTransformer<String, String> transformer = Transformers.identity();
        assertEquals("a string", transformer.transform("a string"));
    }

    @Test
    public void jsonToBytes() {
        final JSON json = JSON_DATA_TYPE.fromJsonString("\"some pop culture reference\"");
        final SafeTransformer<JSON, Bytes> transformer = Transformers.toSuperClass();
        assertEquals(json, transformer.transform(json));
    }

    @Test
    public void binaryToBytes() {
        final Binary json = BINARY_DATA_TYPE.readValue("some pop culture reference".getBytes());
        final SafeTransformer<Binary, Bytes> transformer = Transformers.toSuperClass();
        assertEquals(json, transformer.transform(json));
    }

    @Test
    public void chainSafeTransformers() {
        final SafeTransformer<String, String> transformer0 = Transformers.identity();
        final SafeTransformer<String, String> transformer1 = Transformers.identity();
        final SafeTransformer<String, String> transformer = Transformers.chain(transformer0, transformer1);
        assertEquals("a string", transformer.transform("a string"));
    }

    @Test
    public void chainUnsafeTransformer() throws TransformationException {
        final SafeTransformer<String, String> transformer0 = Transformers.identity();
        final Transformer<String, String> transformer1 = Transformers.identity();
        final Transformer<String, String> transformer = Transformers.chain(transformer0, transformer1);
        assertEquals("a string", transformer.transform("a string"));
    }

    @Test
    public void chain() throws TransformationException {
        final Transformer<String, String> transformer0 = Transformers.identity();
        final Transformer<String, String> transformer1 = Transformers.identity();
        final Transformer<String, String> transformer = Transformers.chain(transformer0, transformer1);
        assertEquals("a string", transformer.transform("a string"));
    }

    @Test
    public void project() {
        final Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        final SafeTransformer<Map<String, String>, String> transformer = Transformers.project("key");
        assertEquals("value", transformer.transform(map));
    }

    @Test
    public void byteArrayToBinary() {
        final byte[] array = "some pop culture reference".getBytes();
        final SafeTransformer<byte[], Binary> transformer = Transformers.byteArrayToBinary();
        assertArrayEquals(array, transformer.transform(array).toByteArray());
    }

    @Test
    public void castTypeReference() throws TransformationException {
        final Map map = new HashMap();

        final Transformer<Map, Map<String, String>> castingTransformer =
            Transformers.cast(new TypeReference<Map<String, String>>() {});

        final Map<String, String> castMap = castingTransformer.transform(map);

        assertSame(map, castMap);
    }

    @Test
    public void castClass() throws TransformationException {
        final Object object = "";

        final Transformer<Object, String> castingTransformer = Transformers.cast(String.class);

        final String castObject = castingTransformer.transform(object);

        assertSame(object, castObject);
    }

    @Test
    public void integer() throws TransformationException {
        final Binary serialisedValue = Transformers.integerToBinary().transform(5);

        final Integer deserialisedValue = Transformers.binaryToInteger().transform(serialisedValue);

        assertEquals(5, (int) deserialisedValue);
    }
}