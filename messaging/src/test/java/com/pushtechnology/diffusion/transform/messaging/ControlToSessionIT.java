package com.pushtechnology.diffusion.transform.messaging;

import static com.pushtechnology.diffusion.client.session.Session.State.CLOSED_BY_CLIENT;
import static com.pushtechnology.diffusion.client.session.Session.State.CONNECTED_ACTIVE;
import static com.pushtechnology.diffusion.client.session.Session.State.CONNECTING;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.verification.VerificationWithTimeout;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.content.Content;
import com.pushtechnology.diffusion.client.features.control.topics.MessagingControl.SendCallback;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.transform.messaging.send.MessageSenderBuilders;
import com.pushtechnology.diffusion.transform.messaging.send.MessageToSessionSender;
import com.pushtechnology.diffusion.transform.messaging.receive.MessageReceiverBuilders;
import com.pushtechnology.diffusion.transform.messaging.receive.MessageReceiverHandle;
import com.pushtechnology.diffusion.transform.messaging.receive.TransformedMessageStream;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * Integration test for sending messages from a session to a handler.
 * @author Matt Champion 12/04/2017
 */
public final class ControlToSessionIT {
    @Mock
    private Session.Listener listener;
    @Mock
    private TransformedMessageStream<Integer> stream;
    @Mock
    private SendCallback sendCallback;

    private Session session;
    private Session controlSession;

    @Before
    public void setUp() {
        initMocks(this);

        session = Diffusion
            .sessions()
            .listener(listener)
            .principal("control")
            .password("password")
            .open("ws://localhost:8080");
        verify(listener, timed()).onSessionStateChanged(session, CONNECTING, CONNECTED_ACTIVE);

        controlSession = Diffusion
            .sessions()
            .listener(listener)
            .principal("control")
            .password("password")
            .open("ws://localhost:8080");
        verify(listener, timed()).onSessionStateChanged(controlSession, CONNECTING, CONNECTED_ACTIVE);
    }

    @After
    public void postConditions() {
        session.close();
        verify(listener, timed()).onSessionStateChanged(session, CONNECTED_ACTIVE, CLOSED_BY_CLIENT);
        controlSession.close();
        verify(listener, timed()).onSessionStateChanged(controlSession, CONNECTED_ACTIVE, CLOSED_BY_CLIENT);

        verifyNoMoreInteractions(listener, stream, sendCallback);
    }

    @Test
    public void sendToHandler() throws TransformationException {
        final MessageReceiverHandle handle = MessageReceiverBuilders
            .newMessageReceiverBuilder()
            .transform(Transformers.<Content>toByteArray())
            .transform(Transformers.byteArrayToBinary())
            .transform(Transformers.binaryToInteger())
            .bind(session)
            .register(stream);

        final MessageToSessionSender<Integer> sender = MessageSenderBuilders
            .newMessageSenderBuilder()
            .transform(Transformers.<Binary, Bytes>cast(Bytes.class))
            .transform(Transformers.integerToBinary())
            .buildToSessionSender(controlSession);

        sender.send(session.getSessionId(), "message/path", 5, sendCallback);

        verify(sendCallback, timed()).onComplete();

        verify(stream, timed()).onMessageReceived("message/path", 5);

        handle.close();
        verify(stream, timed()).onClose();
    }

    private VerificationWithTimeout timed() {
        return timeout(5000L);
    }
}
