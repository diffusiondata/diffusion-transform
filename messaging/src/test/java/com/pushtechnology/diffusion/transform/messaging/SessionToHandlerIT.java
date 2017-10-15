package com.pushtechnology.diffusion.transform.messaging;

import static com.pushtechnology.diffusion.client.session.Session.State.CLOSED_BY_CLIENT;
import static com.pushtechnology.diffusion.client.session.Session.State.CONNECTED_ACTIVE;
import static com.pushtechnology.diffusion.client.session.Session.State.CONNECTING;
import static java.math.BigInteger.TEN;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.math.BigInteger;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.verification.VerificationWithTimeout;

import com.pushtechnology.diffusion.client.Diffusion;
import com.pushtechnology.diffusion.client.features.Messaging.SendCallback;
import com.pushtechnology.diffusion.client.session.Session;
import com.pushtechnology.diffusion.datatype.Bytes;
import com.pushtechnology.diffusion.datatype.binary.Binary;
import com.pushtechnology.diffusion.transform.messaging.receive.MessageReceiverBuilders;
import com.pushtechnology.diffusion.transform.messaging.receive.MessageReceiverHandle;
import com.pushtechnology.diffusion.transform.messaging.receive.TransformedMessageHandler;
import com.pushtechnology.diffusion.transform.messaging.send.MessageSenderBuilders;
import com.pushtechnology.diffusion.transform.messaging.send.MessageToHandlerSender;
import com.pushtechnology.diffusion.transform.transformer.TransformationException;
import com.pushtechnology.diffusion.transform.transformer.Transformers;

/**
 * Integration test for sending messages from a session to a handler.
 * @author Push Technology Limited
 */
public final class SessionToHandlerIT {
    @Mock
    private Session.Listener listener;
    @Mock
    private TransformedMessageHandler<BigInteger> handler;
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

        verifyNoMoreInteractions(listener, handler, sendCallback);
    }

    @Test
    public void sendToHandler() throws TransformationException {
        final MessageReceiverHandle handle = MessageReceiverBuilders
            .newBinaryMessageReceiverBuilder()
            .transform(Transformers.binaryToBigInteger())
            .bind(controlSession)
            .register("message/path", handler);

        verify(handler, timed()).onRegistered("message/path");

        final MessageToHandlerSender<BigInteger> sender = MessageSenderBuilders
            .newMessageSenderBuilder()
            .transform(Transformers.<Binary, Bytes>cast(Bytes.class))
            .transform(Transformers.bigIntegerToBinary())
            .buildToHandlerSender(session);

        sender.send("message/path", TEN, sendCallback);

        verify(sendCallback, timed()).onComplete();

        verify(handler, timed()).onMessageReceived(
            "message/path",
            TEN,
            session.getSessionId(),
            Collections.<String, String>emptyMap());

        handle.close();
        verify(handler, timed()).onClose("message/path");
    }

    private VerificationWithTimeout timed() {
        return timeout(5000L);
    }
}
