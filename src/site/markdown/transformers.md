
# Transformers

A key component is the `Transformer` interface.
This interface provides an abstraction over converting values from one type to another.
It allows for performing deserialsation, data binding and data manipulation.
Importantly it also allows for these transformations to be chained and combined.
The transformation may fail by throwing a `TransformationException`.
There is a variation `SafeTransformer` that does not throw this exception and should always successfully be applicable.
There is another variation `UnsafeTransformer` that was added to simplify exception handling.
It can throw any type of exception and can be passed to the `transformWith` methods of the update and stream builders.
These methods will convert the `UnsafeTransformer` to a `Transformer` that propagates the exception as a
`TransformationException`.
The different method name is to aid type inference when working with lambdas.
In Java 8 this allow methods that throw exceptions to be used in lambdas or as method references.
It is assumed that a transformation applied to nothing results in nothing, null references should be propagated through
transformers.

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

## Provided transformers

See the [JavaDoc](apidocs/index.html?com/pushtechnology/diffusion/transform/transformer/Transformers.html) for
information on the provided transformers.

## Custom transformers

Custom transformers can also be implemented. They should be implemented as stateless functions.

The abstract [FromBinaryTransformer](apidocs/index.html?com/pushtechnology/diffusion/transform/transformer/FromBinaryTransformer.html)
and [ToBinaryTransformer](apidocs/index.html?com/pushtechnology/diffusion/transform/transformer/ToBinaryTransformer.html)
can be used to simplify the implementation of binary transformers. They provide abstract methods that provide a
[DataInput](http://docs.oracle.com/javase/7/docs/api/java/io/DataInput.html) or
[DataOutput](http://docs.oracle.com/javase/7/docs/api/java/io/DataOutput.html) object that can be used to read or write
the Binary value. 
