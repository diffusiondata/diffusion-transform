
# MessageSenderBuilders to sessions

To help with sending messages to sessions a `MessageSenderBuilder` can be used to construct a `MessageSender`.
The `MessageSenderBuilder` makes it easy to chain transformers together.
A `MessageSenderBuilder` is immutable and allows a new `MessageSenderBuilder` to be created from it that adds
an additional transformation.

A `TransformedMessageSender` may throw a `TransformationException` when sending a message if the transformation
cannot be applied.
A `SafeMeassageSender` constructed from `SafeTransformer`s does not throw this exception.

```java
final MessageSender<RandomData> sender = newMessageSenderBuilder()
    .transform(Transformers.<JSON, Bytes>cast(Bytes.class))
    .transform(Transformers.<RandomData>fromPojo())
    .bind(session)
    .build();

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
