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

import static com.pushtechnology.diffusion.transform.transformer.JSONTransformers.JSON_TRANSFORMERS;
import static com.pushtechnology.diffusion.transform.transformer.JacksonContext.JACKSON_CONTEXT;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.datatype.binary.BinaryDataType;
import com.pushtechnology.diffusion.datatype.json.JSON;

/**
 * Common {@link UnsafeTransformer}s and {@link Function}s.
 *
 * @author Push Technology Limited
 */
public final class Transformers {
    private static final BinaryDataType BINARY_DATA_TYPE = Diffusion.dataTypes().binary();
    private static final Function<byte[], Binary> BYTE_ARRAY_TO_BINARY = value -> {
        if (value == null) {
            return null;
        }
        return BINARY_DATA_TYPE.readValue(value);
    };
    private static final Function<Bytes, byte[]> TO_BYTE_ARRAY = value -> {
        if (value == null) {
            return null;
        }
        return value.toByteArray();
    };

    private Transformers() {
    }

    /**
     * Identity transformer.
     *
     * @param <T> the type of value
     * @return a transformer that transforms values to themselves.
     */
    public static <T> Function<T, T> identity() {
        return Function.identity();
    }

    /**
     * Identity transformer.
     *
     * @param type the type of value
     * @param <T> the type of value
     * @return a transformer that transforms values to themselves.
     */
    public static <T> Function<T, T> identity(Class<T> type) {
        return Function.identity();
    }

    /**
     * To super class transformer.
     *
     * @param <S> the type of value
     * @param <T> the super type of value
     * @return a transformer that transforms values to a super class.
     */
    public static <S, T extends S> Function<T, S> toSuperClass() {
        return value -> value;
    }

    /**
     * @return transformer from byte[] to {@link Binary}.
     */
    public static Function<byte[], Binary> byteArrayToBinary() {
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
    public static <S, M, T> UnsafeTransformer<S, T> chain(
            final Function<S, M> transformer0,
            final UnsafeTransformer<M, T> transformer1) {
        return value -> {
            if (value == null) {
                return null;
            }
            final M transientResult = transformer0.apply(value);
            if (transientResult == null) {
                return null;
            }
            return transformer1.transform(transientResult);
        };
    }

    /**
     * Convert a function to an {@link UnsafeTransformer}.
     *
     * @param function the function
     * @param <S> the source value type
     * @param <T> the target value type
     * @return the unsafe transformer
     */
    public static <S, T> UnsafeTransformer<S, T> toTransformer(Function<S, T> function) {
        return function::apply;
    }

    /**
     * A transformer that projects map values.
     *
     * @param key the key to project
     * @param <K> the key type
     * @param <V> the value type
     * @return a transformer that creates a projection of a map
     */
    public static <K, V> Function<Map<K, V>, V> project(final K key) {
        return value -> {
            if (value == null) {
                return null;
            }
            return value.get(key);
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
    public static <S, T> UnsafeTransformer<S, T> cast(Class<T> type) {
        return new UnsafeTransformer<S, T>() {
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
    public static <S, T> UnsafeTransformer<S, T> cast(TypeReference<T> type) {
        return new UnsafeTransformer<S, T>() {
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
    public static <T> UnsafeTransformer<JSON, T> toObject(final Class<T> type) {
        return value -> {
            if (value == null) {
                return null;
            }
            return JACKSON_CONTEXT.toObject(value, type);
        };
    }

    /**
     * Transformer to convert JSON to a type with Jackson.
     *
     * @param typeReference the type reference to convert to
     * @param <T> the target type
     * @return a transformer that converts JSON to the provided type
     */
    public static <T> UnsafeTransformer<JSON, T> toType(final TypeReference<T> typeReference) {
        return value -> {
            if (value == null) {
                return null;
            }
            return JACKSON_CONTEXT.toType(value, typeReference);
        };
    }

    /**
     * Transformer to convert JSON to a map of a specific type of value.
     *
     * @param type the type of value contained by the map
     * @param <T> the type of value contained by the map
     * @return a transformer that converts JSON to a map
     */
    public static <T> UnsafeTransformer<JSON, Map<String, T>> toMapOf(final Class<T> type) {
        return value -> {
            if (value == null) {
                return null;
            }
            return JACKSON_CONTEXT.toMapOf(value, type);
        };
    }

    /**
     * Transformer from pojo to JSON.
     *
     * @param <T> the type of pojo
     * @return the transformer to JSON
     */
    public static <T> UnsafeTransformer<T, JSON> fromPojo() {
        return JSON_TRANSFORMERS.fromPojo();
    }

    /**
     * Transformer from map to JSON.
     *
     * @param <T> the value type of map
     * @return the transformer to JSON
     */
    public static <T> UnsafeTransformer<Map<String, T>, JSON> fromMap() {
        return JSON_TRANSFORMERS.fromMap();
    }

    /**
     * Transformer from big integer to Binary.
     *
     * @return the transformer to Binary
     */
    public static Function<BigInteger, Binary> bigIntegerToBinary() {
        return BigIntegerToBinaryTransformer.INSTANCE;
    }

    /**
     * Transformer from Binary to big integer.
     *
     * @return the transformer to integer
     */
    public static UnsafeTransformer<Binary, BigInteger> binaryToBigInteger() {
        return BinaryToBigIntegerTransformer.INSTANCE;
    }

    /**
     * Transformer from String to JSON.
     * @return the transformer to JSON
     */
    public static UnsafeTransformer<String, JSON> parseJSON() {
        return JSON_TRANSFORMERS.parseJSON();
    }

    /**
     * Transformer from JSON to String.
     * @return the transformer to String
     */
    public static UnsafeTransformer<JSON, String> stringify() {
        return JSON_TRANSFORMERS.stringify();
    }

    /**
     * Transformer from any implementation of Bytes to a byte array.
     * @param <T> the implementation of bytes
     * @return the transformer to byte array
     */
    @SuppressWarnings("unchecked")
    public static <T extends Bytes> Function<T, byte[]> toByteArray() {
        return (Function<T, byte[]>) TO_BYTE_ARRAY;
    }

    /**
     * Create a builder for transformers.
     * @param <V> Value type
     * @return The builder
     */
    public static <V> SafeTransformerBuilder<V, V> builder() {
        return new SafeTransformerBuilderImpl<>(Function.identity());
    }

    /**
     * Create a builder for transformers.
     * @param valueType The value type
     * @param <V> Value type
     * @return The builder
     */
    public static <V> SafeTransformerBuilder<V, V> builder(Class<V> valueType) {
        return new SafeTransformerBuilderImpl<>(Function.identity());
    }
}
