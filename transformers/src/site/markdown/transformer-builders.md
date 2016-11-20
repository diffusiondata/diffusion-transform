
# Transformer builders

To help with transforming values a `TransformerBuilder` can be used to
construct a composite transformer.
The `TransformerBuilder` makes it easy to chain transformers together.
A `TransformerBuilder` is immutable and allows a new `TransformerBuilder` to be
created from it that adds an additional transformation.

Multiple transformations can be chained together, for example to take a POJO
and create a `Binary` object that can be published to a topic.

```java
final SafeTransformer<RandomData, Binary> randomDataSerialiser = Transformers
    .builder(RandomData.class)
    .transform(value -> {
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putInt(value.getId());
        buffer.putLong(value.getTimestamp());
        buffer.putInt(value.getRandomInt());
        return buffer;
    })
    .transform(ByteBuffer::array)
    .transform(byteArrayToBinary())
    .build();
```
