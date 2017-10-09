
# TopicAdderBuilders

_Deprecated since 2.0.0. The ability to initialise a topic with a value when creating it has been deprecated in the
Diffusion API._

To help with creating topics with initial values a `TopicAdderBuilder` can be used to construct a `TopicAdder` that
applies a transformation to a value before creating the topic with the result.
Like the `StreamBuilder` a `TopicAdderBuilder` makes it easy to chain transformers together.
A `TopicAdderBuilder` is immutable and allows a new `TopicAdderBuilder` to be created from it that adds an additional
transformation.

Every `TopicAdderBuilder` has two properties, safe transformation and session binding.
Each of these properties can have one of two values.
Topic adder builders can have any combination of these values.

A `TopicAdderBuilder` that is safely transforming only uses `SafeTransformer`.
If it is transformed with a `Transformer` that is not a `SafeTransformer` the resulting `TopicAdderBuilder` is unsafely
transforming.

A `TopicAdderBuilder` that is not bound must be provided with a `Session` to create a `TopicAdder`.
A `TopicAdderBuilder` that is bound to a session can create a `TopicAdder` without being provided with a `Session`.

A `TopicAdder` will throw a `TransformationException` when attempting to create a topic if the transformation cannot be
applied to the value.
Using only `SafeTransformer`s this additional exception throwing can be avoided.

A `TopicAdder` can be created that converts a bean or pojo to a `JSON` value and uses it to initialise a topic.  

```java
final TopicAdder<String> adder = jsonTopicAdderBuilder()
    .transform(parseJSON())
    .bind(session)
    .create();

try {
    adder.add("json", "{\"foo\":\"bar\"}", new TopicControl.AddCallback.Default());
}
catch (TransformationException e) {
    LOG.error("Initial value could not be parsed as JSON");
    stop();
    return;
}
```
