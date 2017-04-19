
# JSONTransformers

The class `JSONTransformers` provides several transformers
for working with `JSON`. These transformers are backed by
Jackson. The behaviour can be modified by creating an
instance of `JSONTransformers` with `Modules` that will be
registered with the `ObjectMapper`.

```java
final JSONTransformers transformers = new JSONTransformers(new JavaTimeModule());
```
