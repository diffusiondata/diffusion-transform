# Diffusion Transform

This library builds upon the Diffusion API to provide some enhancements to working with `JSON` and `Binary` values.
The Diffusion Java API uses these values when subscribing and updating topics. `JSON` is an opaque data type that does
not allow its value to inspected. `Binary` is a wrapper around a byte array that needs further deserialisation to be
understood. This library aims to add additional ways of transforming these values to those easier to work with.

## Transformers

A key component is the `Transformer` interface. This interface provides an abstraction over converting values from one
type to another. It allows for performing deserialsation, data binding and data manipulation. Importantly it also allows
for these transformations to be chained and combined. The transformation may fail by throwing a
`TransformationException`. There is a variation `SafeTransformer` that does not throw this exception and should always
successfully be applicable.

Provided are several transformers for working with `JSON`, `Binary` and common Java values. There are transformers for
converting pojos and beans to `JSON` and back, accessing the byte array of `Binary` values and chaining transformers
together.

The `@Data` annotation is taken from [Project Lombok](https://projectlombok.org/) to generate a bean like object.

```java
@Data
public class TestBean {
    private String name;
    private int someNumber;
}
```

The `Transformers.toObject` can be used to bind a JSON value to a bean.

```java
final JSON json = JSON_DATA_TYPE.fromJsonString("{\"name\": \"a name\", \"someNumber\": 7}");
final Transformer<JSON, TestBean> transformer = Transformers.toObject(TestBean.class);
final TestBean asBean = transformer.transform(json);
```

## StreamBuilders

To help with receiving values from topics a `StreamBuilder` can be used to construct a stream that delegates to a
`TransformedStream`. The `StreamBuilder` makes it easy to chain transformers together. A `StreamBuilder` is
immutable and allows a new `StreamBuilder` to be created from it that adds an additional transformation.

A `TransformedStream` adds additional error handling capabilities to a `ValueStream` to handle a
`TransformationException`. Using only `SafeTransformer` this additional error handling can be avoided.

Multiple transformations can be chained together to read a `JSON` value and extract part of it.

```java
StreamBuilders.newJsonStreamBuilder()
    .transform(Transformers.toMapOf(BigInteger.class))
    .transform(Transformers.project("timestamp"))
    .create(topics, "json/random", new TransformedStream.Default<JSON, BigInteger>() {
        @Override
        public void onValue(
            String topicPath,
            TopicSpecification topicSpecification,
            BigInteger oldValue,
            BigInteger newValue) {

            LOG.info("New timestamp {}", newValue);
        }
});
```

A `StreamBuilder` can also be used to create a fallback stream. A fallback stream receives notifications for any topics
that do not match the topic selector of any other stream.

A `StreamBuilder` returns a `StreamHandle` when it creates a stream or fallback stream. This must be used to close the
stream when it is no longer needed.

## UpdaterBuilders

To help with updating topics with new values an `UpdaterBuilder` can be used to construct a `TransformedUpdater` that
applies a transformation to a value before updating the topic with the result. Like the `StreamBuilder` an
`UpdaterBuilder` makes it easy to chain transformers together. An `UpdaterBuilder` is immutable and allows a new
`UpdaterBuilder` to be created from it that adds an additional transformation.

A `TransformedUpdater` will throw a `TransformationException` when attempting to update a topic if the transformation
cannot be applied to the value. Using only `SafeTransformer` this additional exception throwing can be avoided.

A `TransformedUpdater` can be created that converts a bean or pojo to a `JSON` value and applies it to a topic.  

```java
final TopicUpdateControl.Updater updater = session
    .feature(TopicUpdateControl.class)
    .updater();

final TransformedUpdater<JSON, RandomData> valueUpdater = updaterBuilder(JSON.class)
    .transform(Transformers.<RandomData>fromPojo())
    .create(updater);

valueUpdater.update(
    "json/random",
    RandomData.next(),
    new TopicUpdateControl.Updater.UpdateCallback.Default());
```

A `UpdaterBuilder` can also be used to register a `TransformedUpdateSource`.

```java
final TransformedUpdater<JSON, RandomData> valueUpdater = updaterBuilder(JSON.class)
    .transform(Transformers.<RandomData>fromPojo())
    .register(updater, "json", new TransformedUpdateSource<JSON, RandomData, TransformedUpdater<JSON, RandomData>>() {
        @Override
        public void onActive(String topicPath, TransformedUpdater<JSON, RandomData> valueUpdater) {
            valueUpdater.update(
                "json/random",
                RandomData.next(),
                new TopicUpdateControl.Updater.UpdateCallback.Default());
        }
    });
```

## Third party components

This library uses Jackson data binding and CBOR data format libraries to work with `JSON` values.
It also uses JUnit, Mockito, Hamcrest and Lombok for testing.

## Licensing

This library is licensed under Apache License, Version 2.0.

Copyright (C) 2016 Push Technology Ltd
