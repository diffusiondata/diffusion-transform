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
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;

/**
 * Context for using Jackson to work with JSON values. The context creates the
 * reusable objects and provides convenience methods for using them to perform
 * data binding operations.
 *
 * @author Push Technology Limited
 */
/*package*/ final class JacksonContext {
    /**
     * The instance of the context.
     */
    /*package*/ static final JacksonContext JACKSON_CONTEXT = new JacksonContext(
        new Module[0],
        Collections.<CBORFactory.Feature, Boolean>emptyMap(),
        Collections.<MapperFeature, Boolean>emptyMap(),
        Collections.<SerializationFeature, Boolean>emptyMap(),
        Collections.<DeserializationFeature, Boolean>emptyMap());
    private static final JSONDataType JSON_DATA_TYPE = Diffusion.dataTypes().json();

    private final CBORFactory factory;
    private final ObjectMapper mapper;
    private final TypeFactory typeFactory;
    private final ObjectReader simpleMapReader;
    private final ObjectWriter simpleMapWriter;

    /**
     * Constructor.
     * @param modules the modules to register with the object mapper
     */
    /*package*/ JacksonContext(
            Module[] modules,
            Map<CBORFactory.Feature, Boolean> cborFeatures,
            Map<MapperFeature, Boolean> mapperFeatures,
            Map<SerializationFeature, Boolean> serializationFeatures,
            Map<DeserializationFeature, Boolean> deserializationFeatures) {

        // Create and configure factory
        factory = new CBORFactory();
        for (Map.Entry<CBORFactory.Feature, Boolean> feature : cborFeatures.entrySet()) {
            factory.configure(feature.getKey(), feature.getValue());
        }

        // Create and configure mapper
        mapper = new ObjectMapper(factory);
        mapper.registerModules(modules);
        for (Map.Entry<MapperFeature, Boolean> feature : mapperFeatures.entrySet()) {
            mapper.configure(feature.getKey(), feature.getValue());
        }
        for (Map.Entry<SerializationFeature, Boolean> feature : serializationFeatures.entrySet()) {
            mapper.configure(feature.getKey(), feature.getValue());
        }
        for (Map.Entry<DeserializationFeature, Boolean> feature : deserializationFeatures.entrySet()) {
            mapper.configure(feature.getKey(), feature.getValue());
        }

        typeFactory = mapper.getTypeFactory();
        final JavaType simpleMapType = typeFactory.constructMapType(Map.class, String.class, Object.class);
        simpleMapReader = mapper.readerFor(simpleMapType);
        simpleMapWriter = mapper.writerFor(simpleMapType);
    }

    /**
     * Convert JSON to an object.
     * @param json the JSON
     * @param type the type of object
     * @param <T> the type of object
     * @return the object
     * @throws TransformationException if the {@link JSON} value could not be bound to the provided type
     */
    public <T> T toObject(JSON json, Class<T> type) throws TransformationException {
        final CBORParser parser = getParser(json);
        try {
            return mapper.readValue(parser, type);
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
    public <T> T toType(JSON json, TypeReference<T> type) throws TransformationException {
        final CBORParser parser = getParser(json);
        try {
            return mapper.readValue(parser, type);
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
    public Map<String, Object> toMap(JSON json) throws TransformationException {
        final CBORParser parser = getParser(json);
        try {
            return simpleMapReader.readValue(parser);
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
    public <T> Map<String, T> toMapOf(JSON json, Class<T> type) throws TransformationException {
        final CBORParser parser = getParser(json);
        try {
            final MapType mapType = typeFactory.constructMapType(Map.class, String.class, type);
            return mapper.readValue(parser, mapType);
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
    public <T> JSON fromPojo(T pojo) throws TransformationException {
        final byte[] pojoBytes;
        try {
            pojoBytes = mapper.writeValueAsBytes(pojo);
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
    public <T> JSON fromMap(Map<String, T> map) throws TransformationException {
        final byte[] mapBytes;
        try {
            mapBytes = simpleMapWriter.writeValueAsBytes(map);
        }
        catch (JsonProcessingException e) {
            throw new TransformationException(e);
        }
        return JSON_DATA_TYPE.readValue(mapBytes);
    }

    private CBORParser getParser(JSON value) throws TransformationException {
        try {
            return factory.createParser(value.asInputStream());
        }
        catch (IOException e) {
            throw new TransformationException(e);
        }
    }

    private void closeParser(CBORParser parser) throws TransformationException {
        try {
            parser.close();
        }
        catch (IOException e) {
            // Could this just discard the exception? The stream the parser operates on is a ByteArrayInputStream
            throw new TransformationException(e);
        }
    }
}
