
# MessageReceiverBuilders

To help with receiving messages a `MessageReceiverBuilder` can be used to register a stream that
delegates to a `MessageStream` or a handler that delegates to a `MessageHandler`.
The `MessageReceiverBuilder` makes it easy to chain transformers together.
A `MessageReceiverBuilder` is immutable and allows a new `MessageReceiverBuilder` to be created from
it that adds an additional transformation.

`TransformedMessageStream`s and `TransformedMessageHandler`s add additional error handling capabilities
to handle a `TransformationException`.
Using only `SafeTransformer`s this additional error handling can be avoided.

A `MessageStream` receives messages sent to sessions.

```java
MessageReceiverBuilders
    .newMessageReceiverBuilder()
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

A `MessageStream` returns a `MessageReceiverHandle` when it creates a stream.
This can be used to close the stream when it is no longer needed.
Since the stream provided when registering a stream using the `MessageReceiverBuilder` is wrapped in an adapter before
it is passed to the Diffusion API the `Messaging` feature cannot be used to close the stream.

A `MessageHandler` receives messages sent to the server.

```java
MessageReceiverBuilders
    .newMessageReceiverBuilder()
    .transform(Transformers.toByteArray())
    .transform(bytes -> Diffusion.dataTypes().json().readValue(bytes))
    .transform(Transformers.stringify())
    .bind(session)
    .register(
        "json/random",
        new TransformedMessageHandler.Default<String>() {
            @Override
            public void onTransformationException(
                    String path,
                    Content value,
                    SessionId sessionId,
                    Map<String, String> sessionProperties,
                    TransformationException e) {
                LOG.warn("{} transformation error, path={}, message={}", this, path, value, e);
            }

            @Override
            public void onMessageReceived(
                    String path,
                    String message,
                    SessionId sessionId,
                    Map<String, String> sessionProperties) {
                LOG.warn("{} message, path={}, message={}", this, path, message);
            }
        });
```

A `MessageHandler` returns a `MessageReceiverHandle` when it creates a handler.
This can be used to close the handler when it is no longer needed.
