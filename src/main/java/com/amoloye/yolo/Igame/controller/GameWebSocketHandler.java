package com.amoloye.yolo.Igame.controller;

import com.amoloye.yolo.Igame.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
@Slf4j
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameService gameService;

    public GameWebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        gameService.addPlayer(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        try {
            gameService.processBet(session, message.getPayload());
        } catch (Exception e) {
            log.error("Error processing message from sessionId={}", session.getId(), e);
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("Error processing your bet."));
                }
            } catch (IOException ioException) {
                log.error("Failed to send error message to sessionId={}", session.getId(), ioException);
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        gameService.removePlayer(session);
    }
}

