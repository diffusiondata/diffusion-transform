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
 * Common {@link Transformer}s.
 *
 * @author Push Technology Limited
 */
@SuppressWarnings("deprecation")
public final class Transformers {
    private static final BinaryDataType BINARY_DATA_TYPE = Diffusion.dataTypes().binary();
    private static final SafeTransformer IDENTITY = new SafeTransformer() {
        @Override
        public Object transform(Object value) {
            return value;
        }
    };
    private static final SafeTransformer<byte[], Binary> BYTE_ARRAY_TO_BINARY = new SafeTransformer<byte[], Binary>() {
        @Override
        public Binary transform(byte[] value) {
            if (value == null) {
                return null;
            }
            return BINARY_DATA_TYPE.readValue(value);
        }
    };
    private static final SafeTransformer<Bytes, byte[]> TO_BYTE_ARRAY = new SafeTransformer<Bytes, byte[]>() {
        @Override
        public byte[] transform(Bytes value) {
            if (value == null) {
                return null;
            }
            return value.toByteArray();
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
                return JACKSON_CONTEXT.toObject(value, type);
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
                return JACKSON_CONTEXT.toType(value, typeReference);
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
                return JACKSON_CONTEXT.toMapOf(value, type);
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
        return JSON_TRANSFORMERS.fromPojo();
    }

    /**
     * Transformer from map to JSON.
     *
     * @param <T> the value type of map
     * @return the transformer to JSON
     */
    @SuppressWarnings("unchecked")
    public static <T> Transformer<Map<String, T>, JSON> fromMap() {
        return JSON_TRANSFORMERS.fromMap();
    }

    /**
     * Transformer from big integer to Binary.
     *
     * @return the transformer to Binary
     */
    public static SafeTransformer<BigInteger, Binary> bigIntegerToBinary() {
        return BigIntegerToBinaryTransformer.INSTANCE;
    }

    /**
     * Transformer from Binary to big integer.
     *
     * @return the transformer to integer
     */
    public static Transformer<Binary, BigInteger> binaryToBigInteger() {
        return BinaryToBigIntegerTransformer.INSTANCE;
    }

    /**
     * Transformer from String to JSON.
     * @return the transformer to JSON
     */
    public static Transformer<String, JSON> parseJSON() {
        return JSON_TRANSFORMERS.parseJSON();
    }

    /**
     * Transformer from JSON to String.
     * @return the transformer to String
     */
    public static Transformer<JSON, String> stringify() {
        return JSON_TRANSFORMERS.stringify();
    }

    /**
     * Transformer from any implementation of Bytes to a byte array.
     * @param <T> the implementation of bytes
     * @return the transformer to byte array
     */
    @SuppressWarnings("unchecked")
    public static <T extends Bytes> SafeTransformer<T, byte[]> toByteArray() {
        return (SafeTransformer<T, byte[]>) TO_BYTE_ARRAY;
    }

    /**
     * Create a {@link Transformer} from a {@link UnsafeTransformer}.
     * @param transformer the uncaught transformer
     * @param <S> the source value type
     * @param <T> the target value type
     * @return the transformer
     */
    public static <S, T> Transformer<S, T> toTransformer(final UnsafeTransformer<S, T> transformer) {
        return new Transformer<S, T>() {
            @Override
            public T transform(S value) throws TransformationException {
                if (value == null) {
                    return null;
                }

                try {
                    return transformer.transform(value);
                }
                catch (TransformationException e) {
                    throw e;
                }
                // CHECKSTYLE.OFF: IllegalCatch // Bulkhead
                catch (Exception e) {
                    throw new TransformationException(e);
                }
                // CHECKSTYLE.ON: IllegalCatch // Bulkhead
            }
        };
    }

    /**
     * Create a builder for transformers.
     * @param <V> Value type
     * @return The builder
     */
    public static <V> SafeTransformerBuilder<V, V> builder() {
        return new SafeTransformerBuilderImpl<>(Transformers.<V>identity());
    }

    /**
     * Create a builder for transformers.
     * @param valueType The value type
     * @param <V> Value type
     * @return The builder
     */
    public static <V> SafeTransformerBuilder<V, V> builder(Class<V> valueType) {
        return new SafeTransformerBuilderImpl<>(identity(valueType));
    }
}
