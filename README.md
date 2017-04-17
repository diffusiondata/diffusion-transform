# Diffusion Transform

This library builds upon the Diffusion API to provide some enhancements to working with `JSON` and `Binary` values.
The Diffusion Java API uses these values when subscribing and updating topics. `JSON` is an opaque data type that does
not allow its value to inspected. `Binary` is a wrapper around a byte array that needs further deserialisation to be
understood. This library aims to add additional ways of transforming these values to those easier to work with.

[![Build Status](https://travis-ci.org/pushtechnology/diffusion-transform.svg?branch=master)](https://travis-ci.org/pushtechnology/diffusion-transform)

[Additional information](https://pushtechnology.github.io/diffusion-transform/)

[Transformers](https://pushtechnology.github.io/diffusion-transform/transformers/transformers.html)

[Transformer builders](https://pushtechnology.github.io/diffusion-transform/transformers/transformer-builders.html)

[Stream builders](https://pushtechnology.github.io/diffusion-transform/streams/stream-builders.html)

[Updater builders](https://pushtechnology.github.io/diffusion-transform/updaters/updater-builders.html)

[Topic adder builders](https://pushtechnology.github.io/diffusion-transform/topic-adders/topic-adder-builders.html)

[Message receivers](https://pushtechnology.github.io/diffusion-transform/messaging/message-receiver-builders.html)

[Message senders](https://pushtechnology.github.io/diffusion-transform/messaging/message-sender-builders.html)

## Third party components

This library uses Jackson data binding and CBOR data format libraries to work with `JSON` values.
It also uses JUnit, Mockito, Hamcrest and Lombok for testing.

## Licensing

This library is licensed under Apache License, Version 2.0.

Copyright (C) 2016 Push Technology Ltd
