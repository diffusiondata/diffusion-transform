
# MessageStreamBuilders

To help with receiving messages a `MessageStreamBuilder` can be used to construct a stream that delegates to a
`MessageStream`.
The `MessageStreamBuilder` makes it easy to chain transformers together.
A `MessageStreamBuilder` is immutable and allows a new `MessageStreamBuilder` to be created from it that adds
an additional transformation.

A `TransformedMessageStream` adds additional error handling capabilities to handle a
`TransformationException`.
Using only `SafeTransformer` this additional error handling can be avoided.

```java
MessageStreamBuilders
    .newMessageStreamBuilder()
    .transform(Transformers.toByteArray())
    .transform(bytes -> Diffusion.dataTypes().json().readValue(bytes))
    .transform(Transformers.stringify())
    .bind(session)
    .register(new TransformedMessageStream.Default<String>() {
        @Override
        public void onTransformationException(String path, Content value, TransformationException e) {
            LOG.warn("{} transformation error, path={}, message={}", this, path, value, e);
        }

        @Override
        public void onMessageReceived(String path, String message) {
            LOG.warn("{} message, path={}, message={}", this, path, message);
        }
    });
```

A `MessageStream` returns a `MessageStreamHandle` when it creates a stream.
This can be used to close the stream when it is no longer needed.
Since the stream provided when registering a stream using the `MessageStreamBuilder` is wrapped in an adapter before
it is passed to the Diffusion API the `Messaging` feature cannot be used to close the stream.
