
# MessageSenderBuilders

To help with sending messages a `MessageSenderBuilder` can be used to construct
`MessageToSessionSender`s and `MessageToHandlerSender`s.
The `MessageSenderBuilder` makes it easy to chain transformers together.
A `MessageSenderBuilder` is immutable and allows a new `MessageSenderBuilder` to be created from it that adds
an additional transformation.

Both `MessageToHandlerSender`s and `MessageToSessionSenders` may throw a `TransformationException` when sending
a message if the transformation cannot be applied.
`SafeMessageToHandlerSender`s and `SafeMessageToSessionSenders` constructed from `SafeTransformer`s do not
throw this exception.

A `MessageToHandlerSender` can be used to send messages to handlers registered with the server.

```java
final MessageToHandlerSender<RandomData> sender = newMessageSenderBuilder()
    .transform(Transformers.<JSON, Bytes>cast(Bytes.class))
    .transform(Transformers.<RandomData>fromPojo())
    .bind(session)
    .buildToHandlerSender();

sender.send(
    "json/random",
    RandomData.next(),
    new Messaging.SendCallback.Default());
```

A `MessageToSessionSender` can be used to send messages to individual and groups of sessions.

```java
final MessageToSessionSender<RandomData> sender = newMessageSenderBuilder()
    .transform(Transformers.<JSON, Bytes>cast(Bytes.class))
    .transform(Transformers.<RandomData>fromPojo())
    .bind(session)
    .buildToSessionSender();

sender.sendToFilter(
    "$Principal IS 'auth'",
    "json/random",
    RandomData.next(),
    new MessagingControl.SendToFilterCallback() {

        @Override
        public void onError(ErrorReason errorReason) {
            LOG.warn("Failed to send message, {}", errorReason);
        }

        @Override
        public void onComplete(int numberSent) {
            LOG.warn("Sent message to {} clients", numberSent);
        }

        @Override
        public void onRejected(Collection<ErrorReport> errors) {
            LOG.warn("Failed to parse filter, {}", errors);
        }
    });
```

