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

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;

/**
 * Context for using Jackson to work with JSON values. The context creates the
 * reusable objects and provides convenience methods for using them to perform
 * data binding operations.
 *
 * @author Push Technology Limited
 */
/*package*/ enum JacksonContext {
    /**
     * The instance of the context.
     */
    INSTANCE;

    private static final JSONDataType JSON_DATA_TYPE = Diffusion.dataTypes().json();

    private final CBORFactory factory = new CBORFactory();
    private final ObjectMapper mapper = new ObjectMapper(factory);
    private final TypeFactory typeFactory = mapper.getTypeFactory();
    private final JavaType simpleMapType = typeFactory.constructMapType(Map.class, String.class, Object.class);
    private final ObjectReader simpleMapReader = mapper.readerFor(simpleMapType);
    private final ObjectWriter simpleMapWriter = mapper.writerFor(simpleMapType);

    private static CBORParser getParser(Bytes value) throws TransformationException {
        try {
            return INSTANCE.factory.createParser(value.asInputStream());
        }
        catch (IOException e) {
            throw new TransformationException(e);
        }
    }

    private static void closeParser(CBORParser parser) throws TransformationException {
        try {
            parser.close();
        }
        catch (IOException e) {
            // Could this just discard the exception? The stream the parser operates on is a ByteArrayInputStream
            throw new TransformationException(e);
        }
    }

    /**
     * Convert JSON to an object.
     * @param json the JSON
     * @param type the type of object
     * @param <T> the type of object
     * @return the object
     * @throws TransformationException if the {@link JSON} value could not be bound to the provided type
     */
    public static <T> T toObject(JSON json, Class<T> type) throws TransformationException {
        final CBORParser parser = getParser(json);
        try {
            return INSTANCE.mapper.readValue(parser, type);
        }
        catch (IOException e) {
            throw new TransformationException(e);
        }
        finally {
            closeParser(parser);
        }
    }

    /**
     * Convert JSON to an object.
     * @param json the JSON
     * @param type the type reference
     * @param <T> the type of object
     * @return the object
     * @throws TransformationException if the {@link JSON} value could not be bound to the provided type
     */
    public static <T> T toType(JSON json, TypeReference<T> type) throws TransformationException {
        final CBORParser parser = getParser(json);
        try {
            return INSTANCE.mapper.readValue(parser, type);
        }
        catch (IOException e) {
            throw new TransformationException(e);
        }
        finally {
            closeParser(parser);
        }
    }

    /**
     * Convert JSON to a map of strings to objects.
     * @param json The JSON
     * @return The map
     * @throws TransformationException if the {@link JSON} value could not be bound to a map
     */
    public static Map<String, Object> toMap(JSON json) throws TransformationException {
        final CBORParser parser = getParser(json);
        try {
            return INSTANCE.simpleMapReader.readValue(parser);
        }
        catch (IOException e) {
            throw new TransformationException(e);
        }
        finally {
            closeParser(parser);
        }
    }

    /**
     * Convert JSON to a map of strings to values of a provided type.
     * @param json the JSON
     * @param type the type of values
     * @return the map
     * @throws TransformationException if the {@link JSON} value could not be bound to a map of string to the provided
     *  type
     */
    public static <T> Map<String, T> toMapOf(JSON json, Class<T> type) throws TransformationException {
        final CBORParser parser = getParser(json);
        try {
            final MapType mapType = INSTANCE.typeFactory.constructMapType(Map.class, String.class, type);
            return INSTANCE.mapper.readValue(parser, mapType);
        }
        catch (IOException e) {
            throw new TransformationException(e);
        }
        finally {
            closeParser(parser);
        }
    }

    /**
     * Construct a JSON object from a POJO.
     * @param pojo the pojo
     * @return the JSON
     * @throws TransformationException if the pojo cannot be bound as {@link JSON}
     */
    public static <T> JSON fromPojo(T pojo) throws TransformationException {
        final byte[] pojoBytes;
        try {
            pojoBytes = INSTANCE.mapper.writeValueAsBytes(pojo);
        }
        catch (JsonProcessingException e) {
            throw new TransformationException(e);
        }
        return JSON_DATA_TYPE.readValue(pojoBytes);
    }

    /**
     * Construct a JSON object from a map.
     * @param map the map
     * @return the JSON
     * @throws TransformationException if the map cannot be bound as {@link JSON}
     */
    public static <T> JSON fromMap(Map<String, T> map) throws TransformationException {
        final byte[] mapBytes;
        try {
            mapBytes = INSTANCE.simpleMapWriter.writeValueAsBytes(map);
        }
        catch (JsonProcessingException e) {
            throw new TransformationException(e);
        }
        return JSON_DATA_TYPE.readValue(mapBytes);
    }
}