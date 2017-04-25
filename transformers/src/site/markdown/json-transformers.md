
# JSONTransformers

The class `JSONTransformers` provides several transformers
for working with `JSON`. These transformers are backed by
Jackson. The behaviour can be modified by creating an
instance of `JSONTransformers` with `Module`s and
`Feature`s that will be registered with the `CBORFactory`
or `ObjectMapper` as appropriate. An immutable builder is
available to help with the configuration.

```java
final JSONTransformers transformers = JSONTransformers
    .builder()
    .registerModule(new JavaTimeModule())
    .configure(WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
    .configure(READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
    .build();
```
