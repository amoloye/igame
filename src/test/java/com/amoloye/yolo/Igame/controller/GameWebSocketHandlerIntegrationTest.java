package com.amoloye.yolo.Igame.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.amoloye.yolo.Igame.service.GameService;

import java.io.IOException;


@ExtendWith(SpringExtension.class)
class GameWebSocketHandlerIntegrationTest {

    @Mock
    private GameService gameService;

    @Mock
    private WebSocketSession session;

    private GameWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GameWebSocketHandler(gameService);
        when(session.getId()).thenReturn("session1");
    }

    @Test
    void testAfterConnectionEstablished_callsAddPlayer() {
        handler.afterConnectionEstablished(session);
        verify(gameService).addPlayer(session);
    }

    @Test
    void testHandleTextMessage_callsProcessBet() {
        String payload = "{\"bet\":\"some-bet\"}";
        TextMessage message = new TextMessage(payload);

        handler.handleTextMessage(session, message);

        verify(gameService).processBet(session, payload);
    }

    @Test
    void testHandleTextMessage_sendsErrorMessageOnException() throws Exception {
        String payload = "{\"bet\":\"some-bet\"}";
        TextMessage message = new TextMessage(payload);

        doThrow(new RuntimeException("processBet failed"))
                .when(gameService).processBet(session, payload);
        when(session.isOpen()).thenReturn(true);

        handler.handleTextMessage(session, message);

        verify(gameService).processBet(session, payload);
        verify(session).sendMessage(argThat(msg -> msg.getPayload().equals("Error processing your bet.")));
    }

    @Test
    void testHandleTextMessage_ignoresIOExceptionWhenSendingErrorMessage() throws Exception {
        String payload = "{\"bet\":\"some-bet\"}";
        TextMessage message = new TextMessage(payload);

        doThrow(new RuntimeException("processBet failed"))
                .when(gameService).processBet(session, payload);
        when(session.isOpen()).thenReturn(true);
        doThrow(new IOException("sendMessage failed"))
                .when(session).sendMessage(any(TextMessage.class));

        handler.handleTextMessage(session, message);

        verify(gameService).processBet(session, payload);
        verify(session).sendMessage(any(TextMessage.class));
    }

    @Test
    void testAfterConnectionClosed_callsRemovePlayer() {
        CloseStatus closeStatus = new CloseStatus(1000, "Normal Closure");

        handler.afterConnectionClosed(session, closeStatus);

        verify(gameService).removePlayer(session);
    }
}
