/*******************************************************************************
 * Copyright (C) 2017 Push Technology Ltd.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.json.JSON;
import com.pushtechnology.diffusion.datatype.json.JSONDataType;

/**
 * Common {@link Transformer}s for working with JSON.
 * <p>
 * Many of the transformations are supported by Jackson. The behaviour of
 * Jackson can be customised by providing modules when constructing the
 * {@link JSONTransformers} that will be registered with the
 * {@link com.fasterxml.jackson.databind.ObjectMapper}.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class JSONTransformers {
    /**
     * Default {@link JSONTransformers}, without any Jackson modules.
     */
    public static final JSONTransformers JSON_TRANSFORMERS = new JSONTransformers(JACKSON_CONTEXT);
    private static final JSONDataType JSON_DATA_TYPE = Diffusion.dataTypes().json();
    private static final UnsafeTransformer<String, JSON> PARSE_JSON = value -> {
        if (value == null) {
            return null;
        }
        return JSON_DATA_TYPE.fromJsonString(value);
    };
    private static final UnsafeTransformer<JSON, String> STRINGIFY_JSON = value -> {
        if (value == null) {
            return null;
        }
        return value.toJsonString();
    };

    private final UnsafeTransformer fromPojo = new UnsafeTransformer() {
        @Override
        public Object transform(Object value) throws TransformationException {
            if (value == null) {
                return null;
            }
            return jacksonContext.fromPojo(value);
        }
    };
    @SuppressWarnings("unchecked")
    private final UnsafeTransformer fromMap = new UnsafeTransformer() {
        @Override
        public Object transform(Object value) throws TransformationException {
            if (value == null) {
                return null;
            }
            return jacksonContext.fromMap((Map<String, ?>) value);
        }
    };
    private final JacksonContext jacksonContext;

    /**
     * Constructor. Allows modules to be registered with Jackson.
     * @param modules th modules to register
     */
    public JSONTransformers(Module... modules) {
        jacksonContext = new JacksonContext(
            modules,
            Collections.emptyMap(),
            Collections.emptyMap(),
            Collections.emptyMap(),
            Collections.emptyMap());
    }

    private JSONTransformers(
            Collection<Module> modules,
            Map<CBORFactory.Feature, Boolean> cborFeatures,
            Map<MapperFeature, Boolean> mapperFeatures,
            Map<SerializationFeature, Boolean> serializationFeatures,
            Map<DeserializationFeature, Boolean> deserializationFeatures) {
        final Module[] modulesArray = new Module[modules.size()];
        modules.toArray(modulesArray);
        jacksonContext = new JacksonContext(
            modulesArray,
            cborFeatures,
            mapperFeatures,
            serializationFeatures,
            deserializationFeatures);
    }

    private JSONTransformers(JacksonContext jacksonContext) {
        this.jacksonContext = jacksonContext;
    }

    /**
     * Transformer to convert JSON to a type with Jackson.
     *
     * @param type the class to convert to
     * @param <T> the target type
     * @return a transformer that converts JSON to the provided type
     */
    public <T> UnsafeTransformer<JSON, T> toObject(final Class<T> type) {
        return value -> {
            if (value == null) {
                return null;
            }
            return jacksonContext.toObject(value, type);
        };
    }

    /**
     * Transformer to convert JSON to a type with Jackson.
     *
     * @param typeReference the type reference to convert to
     * @param <T> the target type
     * @return a transformer that converts JSON to the provided type
     */
    public <T> UnsafeTransformer<JSON, T> toType(final TypeReference<T> typeReference) {
        return value -> {
            if (value == null) {
                return null;
            }
            return jacksonContext.toType(value, typeReference);
        };
    }

    /**
     * Transformer to convert JSON to a map of a specific type of value.
     *
     * @param type the type of value contained by the map
     * @param <T> the type of value contained by the map
     * @return a transformer that converts JSON to a map
     */
    public <T> UnsafeTransformer<JSON, Map<String, T>> toMapOf(final Class<T> type) {
        return value -> {
            if (value == null) {
                return null;
            }
            return jacksonContext.toMapOf(value, type);
        };
    }

    /**
     * Transformer from pojo to JSON.
     *
     * @param <T> the type of pojo
     * @return the transformer to JSON
     */
    @SuppressWarnings("unchecked")
    public <T> UnsafeTransformer<T, JSON> fromPojo() {
        return fromPojo;
    }

    /**
     * Transformer from map to JSON.
     *
     * @param <T> the value type of map
     * @return the transformer to JSON
     */
    @SuppressWarnings("unchecked")
    public <T> UnsafeTransformer<Map<String, T>, JSON> fromMap() {
        return fromMap;
    }

    public UnsafeTransformer getFromPojo() {
        return fromPojo;
    }

    /**
     * Transformer from String to JSON.
     * @return the transformer to JSON
     */
    UnsafeTransformer<String, JSON> parseJSON() {
        return PARSE_JSON;
    }

    public static UnsafeTransformer<JSON, String> getStringifyJson() {
        return STRINGIFY_JSON;
    }

    /**
     * Transformer from JSON to String.
     * @return the transformer to String
     */
    UnsafeTransformer<JSON, String> stringify() {
        return STRINGIFY_JSON;
    }

    /**
     * @return a new immutable builder for {@link JSONTransformers}
     */
    public static Builder builder() {
        return new Builder(
            new ArrayList<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>());
    }

    /**
     * Immutable builder for {@link JSONTransformers}.
     * <p>
     * Allows for Jackson to be customised.
     */
    public static final class Builder {
        private final List<Module> modules;
        private final Map<CBORFactory.Feature, Boolean> cborFeatures;
        private final Map<MapperFeature, Boolean> mapperFeatures;
        private final Map<SerializationFeature, Boolean> serializationFeatures;
        private final Map<DeserializationFeature, Boolean> deserializationFeatures;

        private Builder(
                List<Module> modules,
                Map<CBORFactory.Feature, Boolean> cborFeatures,
                Map<MapperFeature, Boolean> mapperFeatures,
                Map<SerializationFeature, Boolean> serializationFeatures,
                Map<DeserializationFeature, Boolean> deserializationFeatures) {
            this.modules = modules;
            this.cborFeatures = cborFeatures;
            this.mapperFeatures = mapperFeatures;
            this.serializationFeatures = serializationFeatures;
            this.deserializationFeatures = deserializationFeatures;
        }

        /**
         * Register a new Jackson module.
         * @return a new builder
         */
        public Builder registerModule(Module module) {
            final List<Module> newModules = new ArrayList<>(modules);
            newModules.add(module);
            return new Builder(
                newModules,
                cborFeatures,
                mapperFeatures,
                serializationFeatures,
                deserializationFeatures);
        }

        /**
         * Configure a CBOR feature.
         * @return a new builder
         */
        public Builder configure(CBORFactory.Feature feature, boolean enabled) {
            final Map<CBORFactory.Feature, Boolean> newCborFeatures = new HashMap<>(cborFeatures);
            newCborFeatures.put(feature, enabled);
            return new Builder(
                modules,
                cborFeatures,
                mapperFeatures,
                serializationFeatures,
                deserializationFeatures);
        }

        /**
         * Configure a serialisation feature.
         * @return a new builder
         */
        public Builder configure(MapperFeature feature, boolean enabled) {
            final Map<MapperFeature, Boolean> newMapperFeatures =
                new HashMap<>(mapperFeatures);
            newMapperFeatures.put(feature, enabled);
            return new Builder(
                modules,
                cborFeatures,
                newMapperFeatures,
                serializationFeatures,
                deserializationFeatures);
        }

        /**
         * Configure a serialisation feature.
         * @return a new builder
         */
        public Builder configure(SerializationFeature feature, boolean enabled) {
            final Map<SerializationFeature, Boolean> newSerializationFeatures =
                new HashMap<>(serializationFeatures);
            newSerializationFeatures.put(feature, enabled);
            return new Builder(
                modules,
                cborFeatures,
                mapperFeatures,
                newSerializationFeatures,
                deserializationFeatures);
        }

        /**
         * Configure a deserialisation feature.
         * @return a new builder
         */
        public Builder configure(DeserializationFeature feature, boolean enabled) {
            final Map<DeserializationFeature, Boolean> newDeserializationFeatures =
                new HashMap<>(deserializationFeatures);
            newDeserializationFeatures.put(feature, enabled);
            return new Builder(
                modules,
                cborFeatures,
                mapperFeatures,
                serializationFeatures,
                newDeserializationFeatures);
        }

        /**
         * @return a new instance of {@link JSONTransformers}
         */
        public JSONTransformers build() {
            return new JSONTransformers(
                modules,
                cborFeatures,
                mapperFeatures,
                serializationFeatures,
                deserializationFeatures);
        }
    }
}
