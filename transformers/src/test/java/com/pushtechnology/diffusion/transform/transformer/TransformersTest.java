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
import static com.pushtechnology.diffusion.transform.transformer.Transformers.cast;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.parseJSON;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.stringify;
import static com.pushtechnology.diffusion.transform.transformer.Transformers.toSuperClass;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.binary.BinaryDataType;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;

import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;

/**
 * Unit tests for {@link Transformers}.
 *
 * @author Push Technology Limited
 */
public final class TransformersTest {
    private static final JSONDataType JSON_DATA_TYPE = Diffusion.dataTypes().json();
    private static final BinaryDataType BINARY_DATA_TYPE = Diffusion.dataTypes().binary();

    @Test
    public void toObject() throws Exception {
        final JSON json = JSON_DATA_TYPE.fromJsonString("{\"name\": \"a name\", \"someNumber\": 7}");
        final UnsafeTransformer<JSON, TestBean> transformer = Transformers.toObject(TestBean.class);
        final TestBean asBean = transformer.transform(json);
        assertEquals(asBean.getName(), "a name");
        assertEquals(asBean.getSomeNumber(), 7);
    }

    @Test
    public void toObjectNull() throws Exception {
        final UnsafeTransformer<JSON, TestBean> transformer = Transformers.toObject(TestBean.class);
        final TestBean asBean = transformer.transform(null);
        assertNull(asBean);
    }

    @Test
    public void toType() throws Exception {
        final JSON json = JSON_DATA_TYPE.fromJsonString("\"some pop culture reference\"");
        final UnsafeTransformer<JSON, String> transformer = Transformers.toType(new TypeReference<String>() {});
        final String asString = transformer.transform(json);
        assertEquals(asString, "some pop culture reference");
    }

    @Test
    public void toTypeNull() throws Exception {
        final UnsafeTransformer<JSON, String> transformer = Transformers.toType(new TypeReference<String>() {});
        final String asString = transformer.transform(null);
        assertNull(asString);
    }

    @Test
    public void toMapOf() throws Exception {
        final JSON json = JSON_DATA_TYPE.fromJsonString("{\"key\": \"value\"}");
        final UnsafeTransformer<JSON, Map<String, String>> transformer = Transformers.toMapOf(String.class);
        final Map<String, String> asMap = transformer.transform(json);
        assertThat(asMap, new IsMapContaining<>(equalTo("key"), equalTo("value")));
    }

    @Test
    public void toMapOfNull() throws Exception {
        final UnsafeTransformer<JSON, Map<String, String>> transformer = Transformers.toMapOf(String.class);
        final Map<String, String> asMap = transformer.transform(null);
        assertNull(asMap);
    }

    @Test
    public void fromPojo() throws Exception {
        final TestBean bean = new TestBean();
        bean.setName("a name");
        bean.setSomeNumber(7);

        final UnsafeTransformer<TestBean, JSON> transformer = Transformers.fromPojo();
        final JSON json = transformer.transform(bean);
        final Map<String, ?> asMap = JACKSON_CONTEXT.toMap(json);
        assertThat(asMap, new IsMapContaining<>(equalTo("name"), CoreMatchers.<Object>equalTo("a name")));
        assertThat(asMap, new IsMapContaining<>(equalTo("someNumber"), CoreMatchers.<Object>equalTo(7)));
    }

    @Test
    public void fromPojoNull() throws Exception {
        final UnsafeTransformer<TestBean, JSON> transformer = Transformers.fromPojo();
        final JSON json = transformer.transform(null);
        assertNull(json);
    }

    @Test
    public void fromMap() throws Exception {
        final Map<String, String> sourceMap = new HashMap<>();
        sourceMap.put("key", "value");
        final UnsafeTransformer<Map<String, String>, JSON> transformer = Transformers.fromMap();
        final JSON json = transformer.transform(sourceMap);
        final Map<String, String> asMap = JACKSON_CONTEXT.toMapOf(json, String.class);
        assertThat(asMap, new IsMapContaining<>(equalTo("key"), equalTo("value")));
    }

    @Test
    public void fromMapNull() throws Exception {
        final UnsafeTransformer<Map<String, String>, JSON> transformer = Transformers.fromMap();
        final JSON json = transformer.transform(null);
        assertNull(json);
    }

    @Test
    public void identity() throws Exception {
        final Function<String, String> transformer = Transformers.identity();
        assertEquals("a string", transformer.apply("a string"));
    }

    @Test
    public void identityWithType() {
        final Function<String, String> transformer = Transformers.identity(String.class);
        assertEquals("a string", transformer.apply("a string"));
    }

    @Test
    public void identifyNull() throws TransformationException {
        final Function<JSON, JSON> transformer = Transformers.identity();
        final JSON json = transformer.apply(null);
        assertNull(json);
    }

    @Test
    public void jsonToBytes() {
        final JSON json = JSON_DATA_TYPE.fromJsonString("\"some pop culture reference\"");
        final Function<JSON, Bytes> transformer = toSuperClass();
        assertEquals(json, transformer.apply(json));
    }

    @Test
    public void jsonToBytesNull() {
        final Function<JSON, Bytes> transformer = toSuperClass();
        final Bytes bytes = transformer.apply(null);
        assertNull(bytes);
    }

    @Test
    public void binaryToBytes() {
        final Binary json = BINARY_DATA_TYPE.readValue("some pop culture reference".getBytes());
        final Function<Binary, Bytes> transformer = toSuperClass();
        assertEquals(json, transformer.apply(json));
    }

    @Test
    public void binaryToBytesNull() {
        final Function<Binary, Bytes> transformer = toSuperClass();
        final Bytes bytes = transformer.apply(null);
        assertNull(bytes);
    }

    @Test
    public void chainUnsafeTransformer() throws Exception {
        final Function<String, String> transformer0 = Transformers.identity();
        final UnsafeTransformer<String, String> transformer1 = value -> value;
        final UnsafeTransformer<String, String> transformer = Transformers.chain(transformer0, transformer1);
        assertEquals("a string", transformer.transform("a string"));
    }

    @Test
    public void project() {
        final Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        final Function<Map<String, String>, String> transformer = Transformers.project("key");
        assertEquals("value", transformer.apply(map));
    }

    @Test
    public void projectNull() {
        final Function<Map<String, String>, String> transformer = Transformers.project("key");
        assertNull(transformer.apply(null));
    }

    @Test
    public void byteArrayToBinary() {
        final byte[] array = "some pop culture reference".getBytes();
        final Function<byte[], Binary> transformer = Transformers.byteArrayToBinary();
        assertArrayEquals(array, transformer.apply(array).toByteArray());
    }

    @Test
    public void byteArrayToBinaryNull() {
        final Function<byte[], Binary> transformer = Transformers.byteArrayToBinary();
        assertNull(transformer.apply(null));
    }

    @Test
    public void castTypeReference() throws Exception {
        final Map map = new HashMap();

        final UnsafeTransformer<Map, Map<String, String>> castingTransformer =
            cast(new TypeReference<Map<String, String>>() {});

        final Map<String, String> castMap = castingTransformer.transform(map);

        assertSame(map, castMap);
    }

    @Test
    public void castTypeReferenceNull() throws Exception {
        final UnsafeTransformer<Map, Map<String, String>> castingTransformer =
            cast(new TypeReference<Map<String, String>>() {});

        final Map<String, String> castMap = castingTransformer.transform(null);

        assertNull(castMap);
    }

    @Test
    public void castClass() throws Exception {
        final Object object = "";

        final UnsafeTransformer<Object, String> castingTransformer = cast(String.class);

        final String castObject = castingTransformer.transform(object);

        assertSame(object, castObject);
    }

    @Test
    public void castClassNull() throws Exception {
        final UnsafeTransformer<Object, String> castingTransformer = cast(String.class);

        final String castObject = castingTransformer.transform(null);

        assertNull(castObject);
    }

    @Test
    public void jsonString() throws Exception {
        final JSON serialisedValue = parseJSON().transform("{\"key\":\"value\"}");

        assertEquals(JSON_DATA_TYPE.fromJsonString("{\"key\":\"value\"}"), serialisedValue);
        final Map<String, String> map = Transformers.toMapOf(String.class).transform(serialisedValue);
        assertEquals("value", map.get("key"));
        assertEquals(1, map.size());

        final String deserialisedValue = stringify().transform(serialisedValue);
        assertEquals("{\"key\":\"value\"}", deserialisedValue);
    }

    @Test
    public void jsonStringNull() throws Exception {
        final JSON serialisedValue = parseJSON().transform(null);
        assertNull(serialisedValue);

        final String deserialisedValue = stringify().transform(null);
        assertNull(deserialisedValue);
    }

    @Test
    public void testAsTransformer() throws Exception {
        final UnsafeTransformer<String, String> transformer = Transformers.toTransformer(Function.<String>identity());

        final String value = transformer.transform(null);
        assertNull(value);
    }
}
