package com.amoloye.yolo.Igame.service;

import com.amoloye.yolo.Igame.requestDto.Bet;
import com.amoloye.yolo.Igame.responseDto.PlayerBet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

class RoundManagerTest {

    private RoundManager roundManager;
    private ObjectMapper objectMapper;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        session = mock(WebSocketSession.class);
        roundManager = new RoundManager(objectMapper);
    }

    @Test
    void testExecuteRound_sendsMessageToPlayer() throws Exception {
        when(session.isOpen()).thenReturn(true);
        when(objectMapper.writeValueAsString(any())).thenReturn(
                "[{\"nickname\":\"Alice\",\"winnings\":9.9}]");

        Bet bet = new Bet("Alice", 5, BigDecimal.ONE);
        PlayerBet playerBet = new PlayerBet(session, bet);

        roundManager.executeRound(List.of(playerBet), Set.of(session));

        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
    }
}