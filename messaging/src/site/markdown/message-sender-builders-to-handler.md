
# MessageSenderBuilders to handlers

To help with sending messages to handlers a `MessageSenderBuilder` can be used to construct a `MessageSender`.
The `MessageSenderBuilder` makes it easy to chain transformers together.
A `MessageSenderBuilder` is immutable and allows a new `MessageSenderBuilder` to be created from it that adds
an additional transformation.

A `TransformedMessageSender` may throw a `TransformationException` when sending a message if the transformation
cannot be applied.
A `SafeMeassageSender` constructed from `SafeTransformer`s does not throw this exception.

```java
final TransformedMessageSender<RandomData> sender = newMessageSenderBuilder()
    .transform(Transformers.<JSON, Bytes>cast(Bytes.class))
    .transform(Transformers.<RandomData>fromPojo())
    .bind(session)
    .build();

sender.send(
    "json/random",
    RandomData.next(),
    new Messaging.SendCallback.Default());
```
