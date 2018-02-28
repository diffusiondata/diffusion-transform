
# UpdaterBuilders

To help with updating topics with new values an `UpdaterBuilder` can be used to construct a `TransformedUpdater` that
applies a transformation to a value before updating the topic with the result. Like the `StreamBuilder` an
`UpdaterBuilder` makes it easy to chain transformers together. An `UpdaterBuilder` is immutable and allows a new
`UpdaterBuilder` to be created from it that adds an additional transformation.

Every `UpdaterBuilder` has two properties, safe transformation and session binding. Each of these properties can have
one of two values. Updater builders can have any combination of these values.

### Safe transforming builders

An `UpdaterBuilder` that is safely transforming only uses `Function`s. If it is transformed with a `Transformer` the
resulting `UpdaterBuilder` is unsafely transforming.

### Unsafe transforming builders

An `UpdaterBuilder` that is not bound must be provided with an `Updater` to create a `TransformedUpdater` or a
`TopicUpdateControl` to register a `TransformedUpdateSource`. An `UpdaterBuilder` that is bound to a session can create
a `TransformedUpdater` or register a `TransformedUpdateSource` without any being provided  an `Updater` or
`TopicUpdateControl`.

A `TransformedUpdater` will throw a `TransformationException` when attempting to update a topic if the transformation
cannot be applied to the value. Using only `Function` this additional exception throwing can be avoided.

### Creating transformed updaters

A `TransformedUpdater` can be created that converts a bean or pojo to a `JSON` value and applies it to a topic.  

```java
final TopicUpdateControl.Updater updater = session
    .feature(TopicUpdateControl.class)
    .updater();

final TransformedUpdater<JSON, RandomData> valueUpdater = updaterBuilder(JSON.class)
    .unsafeTransform(Transformers.<RandomData>fromPojo())
    .create(updater);

valueUpdater.update(
    "json/random",
    RandomData.next(),
    new TopicUpdateControl.Updater.UpdateCallback.Default());
```

A `UpdaterBuilder` can also be used to register a `TransformedUpdateSource`.

```java
final TransformedUpdater<JSON, RandomData> valueUpdater = updaterBuilder(JSON.class)
    .unsafeTransform(Transformers.<RandomData>fromPojo())
    .register(session.feature(TopicUpdateControl.class), "json", new TransformedUpdateSource.Default<JSON, RandomData>() {
        @Override
        public void onActive(String topicPath, TransformedUpdater<JSON, RandomData> valueUpdater) {
            valueUpdater.update(
                "json/random",
                RandomData.next(),
                new TopicUpdateControl.Updater.UpdateCallback.Default());
        }
    });
```

### Updating time series topics

An `UpdaterBuilder` can also create a `TimeSeriesUpdater` that can append or edit transformed values to time series
topics.
