/*******************************************************************************
 * Copyright (C) 2016 Push Technology Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.pushtechnology.diffusion.examples.runnable;

import static com.pushtechnology.diffusion.transform.stream.StreamBuilders.newBinaryStreamBuilder;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pushtechnology.diffusion.client.features.Topics;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.client.topics.details.TopicSpecification;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.transform.stream.TransformedStream;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * A client that consumes Binary topics containing a string based timestamp.
 *
 * @author Push Technology Limited
 */
public final class ConsumingTimestamp extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumingBinary.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    /**
     * Constructor.
     * @param url The URL to connect to
     * @param principal The principal to connect as
     */
    public ConsumingTimestamp(String url, String principal) {
        super(url, principal);
    }

    @Override
    public void onConnected(Session session) {
        final Topics topics = session.feature(Topics.class);

        newBinaryStreamBuilder()
            .transform(Transformers.binaryToString(Charset.forName("UTF-8")))
            // This method reference may throw a ParseException that will be
            // converted to a TransformationException automatically
            .transformSafely(DATE_FORMAT::parse)
            .transform(Date::toInstant)
            .register(topics, "binary/timestamp", new TransformedStream.Default<Binary, Instant>() {
                @Override
                public void onValue(
                    String topicPath,
                    TopicSpecification topicSpecification,
                    Instant oldValue,
                    Instant newValue) {

                    LOG.info("New timestamp {}", newValue);
                }
            });

        topics.subscribe("binary/timestamp", new Topics.CompletionCallback.Default());
    }

    /**
     * Entry point for the example.
     * @param args The command line arguments
     * @throws InterruptedException If the main thread was interrupted
     */
    // CHECKSTYLE.OFF: UncommentedMain
    public static void main(String[] args) throws InterruptedException {
        final ConsumingTimestamp client =
            new ConsumingTimestamp("ws://diffusion.example.com:80", "auth");
        client.start("auth_secret");
        client.waitForStopped();
    }
    // CHECKSTYLE.ON: UncommentedMain
}
