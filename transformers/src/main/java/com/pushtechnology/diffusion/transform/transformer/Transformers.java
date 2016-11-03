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

import java.nio.charset.Charset;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.InvalidDataException;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Common {@link Transformer}s.
 *
 * @author Push Technology Limited
 */
public final class Transformers {
    private static final SafeTransformer IDENTITY = new SafeTransformer() {
        @Override
        public Object transform(Object value) {
            return value;
        }
    };
    private static final SafeTransformer<byte[], Binary> BYTE_ARRAY_TO_BINARY = new SafeTransformer<byte[], Binary>() {
        @Override
        public Binary transform(byte[] value) {
            return Diffusion.dataTypes().binary().readValue(value);
        }
    };
    private static final Transformer FROM_POJO_TRANSFORMER = new Transformer() {
        @Override
        public Object transform(Object value) throws TransformationException {
            if (value == null) {
                return null;
            }
            return JacksonContext.fromPojo(value);
        }
    };
    @SuppressWarnings("unchecked")
    private static final Transformer FROM_MAP_TRANSFORMER = new Transformer() {
        @Override
        public Object transform(Object value) throws TransformationException {
            if (value == null) {
                return null;
            }
            return JacksonContext.fromMap((Map<String, ?>) value);
        }
    };
    private static final Transformer<String, JSON> PARSE_JSON_TRANSFORMER = new Transformer<String, JSON>() {
        @Override
        public JSON transform(String value) throws TransformationException {
            try {
                return Diffusion.dataTypes().json().fromJsonString(value);
            }
            catch (InvalidDataException e) {
                throw new TransformationException(e);
            }
        }
    };
    private static final Transformer<JSON, String> STRINGIFY_TRANSFORMER = new Transformer<JSON, String>() {
        @Override
        public String transform(JSON value) throws TransformationException {
            try {
                return value.toJsonString();
            }
            catch (InvalidDataException e) {
                throw new TransformationException(e);
            }
        }
    };

    private Transformers() {
    }

    /**
     * Identity transformer.
     *
     * @param <T> the type of value
     * @return a transformer that transforms values to themselves.
     */
    @SuppressWarnings("unchecked")
    public static <T> SafeTransformer<T, T> identity() {
        return (SafeTransformer<T, T>) IDENTITY;
    }

    /**
     * Identity transformer.
     *
     * @param type the type of value
     * @param <T> the type of value
     * @return a transformer that transforms values to themselves.
     */
    @SuppressWarnings("unchecked")
    public static <T> SafeTransformer<T, T> identity(Class<T> type) {
        return IDENTITY;
    }

    /**
     * To super class transformer.
     *
     * @param <S> the type of value
     * @param <T> the super type of value
     * @return a transformer that transforms values to a super class.
     */
    @SuppressWarnings("unchecked")
    public static <S, T extends S> SafeTransformer<T, S> toSuperClass() {
        return IDENTITY;
    }

    /**
     * @return transformer from byte[] to {@link Binary}.
     */
    public static SafeTransformer<byte[], Binary> byteArrayToBinary() {
        return BYTE_ARRAY_TO_BINARY;
    }

    /**
     * Chain two transformers together.
     *
     * @param transformer0 the first transformer
     * @param transformer1 the second transformer
     * @param <S> the source value type
     * @param <M> the intermediate value type
     * @param <T> the target value type
     * @return the composed transformer
     */
    public static <S, M, T> Transformer<S, T> chain(
            final Transformer<S, M> transformer0,
            final Transformer<M, T> transformer1) {
        return new Transformer<S, T>() {
            @Override
            public T transform(S value) throws TransformationException {
                if (value == null) {
                    return null;
                }
                final M transientValue = transformer0.transform(value);
                return transformer1.transform(transientValue);
            }
        };
    }

    /**
     * Chain two safe transformers together.
     *
     * @param transformer0 the first transformer
     * @param transformer1 the second transformer
     * @param <S> the source value type
     * @param <M> the intermediate value type
     * @param <T> the target value type
     * @return the composed transformer
     */
    public static <S, M, T> SafeTransformer<S, T> chain(
            final SafeTransformer<S, M> transformer0,
            final SafeTransformer<M, T> transformer1) {
        return new SafeTransformer<S, T>() {
            @Override
            public T transform(S value) {
                if (value == null) {
                    return null;
                }
                final M transientValue = transformer0.transform(value);
                return transformer1.transform(transientValue);
            }
        };
    }

    /**
     * A transformer that projects map values.
     *
     * @param key the key to project
     * @param <K> the key type
     * @param <V> the value type
     * @return a transformer that creates a projection of a map
     */
    public static <K, V> SafeTransformer<Map<K, V>, V> project(final K key) {
        return new SafeTransformer<Map<K, V>, V>() {
            @Override
            public V transform(Map<K, V> value) {
                if (value == null) {
                    return null;
                }
                return value.get(key);
            }
        };
    }

    /**
     * A transformer that casts to the target type.
     *
     * @param type the type value describing the type to cast to
     * @param <S> the type to cast from
     * @param <T> the type to cast to
     * @return a transformer that casts the object
     */
    public static <S, T> Transformer<S, T> cast(Class<T> type) {
        return new Transformer<S, T>() {
            // Cast exceptions are caught so they can be propagated as transformation exceptions
            @SuppressWarnings("unchecked")
            @Override
            public T transform(S value) throws TransformationException {
                try {
                    return (T) value;
                }
                catch (ClassCastException e) {
                    throw new TransformationException(e);
                }
            }
        };
    }

    /**
     * A transformer that casts to the target type.
     *
     * @param type the type reference describing the type to cast to
     * @param <S> the type to cast from
     * @param <T> the type to cast to
     * @return a transformer that casts the object
     */
    public static <S, T> Transformer<S, T> cast(TypeReference<T> type) {
        return new Transformer<S, T>() {
            // Cast exceptions are caught so they can be propagated as transformation exceptions
            @SuppressWarnings("unchecked")
            @Override
            public T transform(S value) throws TransformationException {
                try {
                    return (T) value;
                }
                catch (ClassCastException e) {
                    throw new TransformationException(e);
                }
            }
        };
    }

    /**
     * Transformer to convert JSON to a type with Jackson.
     *
     * @param type the class to convert to
     * @param <T> the target type
     * @return a transformer that converts JSON to the provided type
     */
    public static <T> Transformer<JSON, T> toObject(final Class<T> type) {
        return new Transformer<JSON, T>() {
            @Override
            public T transform(JSON value) throws TransformationException {
                if (value == null) {
                    return null;
                }
                return JacksonContext.toObject(value, type);
            }
        };
    }

    /**
     * Transformer to convert JSON to a type with Jackson.
     *
     * @param typeReference the type reference to convert to
     * @param <T> the target type
     * @return a transformer that converts JSON to the provided type
     */
    public static <T> Transformer<JSON, T> toType(final TypeReference<T> typeReference) {
        return new Transformer<JSON, T>() {
            @Override
            public T transform(JSON value) throws TransformationException {
                if (value == null) {
                    return null;
                }
                return JacksonContext.toType(value, typeReference);
            }
        };
    }

    /**
     * Transformer to convert JSON to a map of a specific type of value.
     *
     * @param type the type of value contained by the map
     * @param <T> the type of value contained by the map
     * @return a transformer that converts JSON to a map
     */
    public static <T> Transformer<JSON, Map<String, T>> toMapOf(final Class<T> type) {
        return new Transformer<JSON, Map<String, T>>() {
            @Override
            public Map<String, T> transform(JSON value) throws TransformationException {
                if (value == null) {
                    return null;
                }
                return JacksonContext.toMapOf(value, type);
            }
        };
    }

    /**
     * Transformer from pojo to JSON.
     *
     * @param <T> the type of pojo
     * @return the transformer to JSON
     */
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, JSON> fromPojo() {
        return FROM_POJO_TRANSFORMER;
    }

    /**
     * Transformer from map to JSON.
     *
     * @param <T> the value type of map
     * @return the transformer to JSON
     */
    @SuppressWarnings("unchecked")
    public static <T> Transformer<Map<String, T>, JSON> fromMap() {
        return FROM_MAP_TRANSFORMER;
    }

    /**
     * Transformer from integer to Binary.
     *
     * @return the transformer to Binary
     */
    public static Transformer<Integer, Binary> integerToBinary() {
        return IntegerToBinaryTransformer.INSTANCE;
    }

    /**
     * Transformer from Binary to integer.
     *
     * @return the transformer to integer
     */
    public static Transformer<Binary, Integer> binaryToInteger() {
        return BinaryToIntegerTransformer.INSTANCE;
    }

    /**
     * Transformer from String to Binary.
     *
     * @param charset the character set to encode the string with
     * @return the transformer to Binary
     */
    public static Transformer<String, Binary> stringToBinary(Charset charset) {
        return new StringToBinaryTransformer(charset);
    }

    /**
     * Transformer from Binary to String.
     *
     * @param charset the character set to decode the string with
     * @return the transformer to String
     */
    public static Transformer<Binary, String> binaryToString(Charset charset) {
        return new BinaryToStringTransformer(charset);
    }

    /**
     * Transformer from String to JSON.
     * @return the transformer to JSON
     */
    public static Transformer<String, JSON> parseJSON() {
        return PARSE_JSON_TRANSFORMER;
    }

    /**
     * Transformer from JSON to String.
     * @return the transformer to String
     */
    public static Transformer<JSON, String> stringify() {
        return STRINGIFY_TRANSFORMER;
    }
}
