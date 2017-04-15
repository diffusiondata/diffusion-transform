
# MessageHandlerBuilders

To help with receiving messages a `MessageHandlerBuilder` can be used to construct a stream that delegates to a
`MessageHandler`.
The `MessageHandlerBuilder` makes it easy to chain transformers together.
A `MessageHandlerBuilder` is immutable and allows a new `MessageHandlerBuilder` to be created from it that adds
an additional transformation.

A `TransformedMessageHandler` adds additional error handling capabilities to handle a
`TransformationException`.
Using only `SafeTransformer` this additional error handling can be avoided.

```java
MessageHandlerBuilders
    .newMessageHandlerBuilder()
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

A `MessageHandler` returns a `MessageHandlerHandle` when it creates a handler.
This can be used to close the handler when it is no longer needed.
