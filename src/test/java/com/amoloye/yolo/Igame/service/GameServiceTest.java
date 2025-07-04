package com.amoloye.yolo.Igame.service;

import com.amoloye.yolo.Igame.requestDto.Bet;
import org.junit.jupiter.api.*;
import org.springframework.web.socket.WebSocketSession;
import java.math.BigDecimal;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private BetProcessor betProcessor;
    private RoundManager roundManager;
    private WebSocketSession session;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        betProcessor = mock(BetProcessor.class);
        roundManager = mock(RoundManager.class);
        session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session1");

        gameService = new GameService(betProcessor, roundManager);
    }

    @Test
    void testAddAndRemovePlayer() {
        gameService.addPlayer(session);
        gameService.removePlayer(session);
        // No exceptions = pass
    }

    @Test
    void testProcessBet_validUniqueNickname() {
        String json = "{\"bet\":\"value\"}";
        Bet bet = new Bet("player1", 5, BigDecimal.TEN);

        when(betProcessor.process(json)).thenReturn(Optional.of(bet));

        gameService.processBet(session, json);

        verify(betProcessor).process(json);
        verify(betProcessor, never()).sendInvalidMessage(any());
        verify(betProcessor, never()).sendMessage(any(), contains("Nickname already used"));
    }

    @Test
    void testProcessBet_duplicateNickname_differentSession() {
        String json = "{\"bet\":\"value\"}";

        // Real bets to ensure consistent nickname comparison
        Bet bet1 = new Bet("dupeNick", 5, BigDecimal.TEN);
        Bet bet2 = new Bet("DUPEnick", 5, BigDecimal.TEN); // case-insensitive collision

        when(betProcessor.process(json))
                .thenReturn(Optional.of(bet1))
                .thenReturn(Optional.of(bet2));

        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session1.getId()).thenReturn("s1");
        when(session2.getId()).thenReturn("s2");

        gameService.processBet(session1, json); // should succeed
        gameService.processBet(session2, json); // should be rejected

        verify(betProcessor, times(2)).process(json);
        verify(betProcessor).sendMessage(eq(session2), contains("Nickname already used"));
    }

    @Test
    void testProcessBet_invalidBet() {
        String json = "{\"bet\":\"invalid\"}";

        when(betProcessor.process(json)).thenReturn(Optional.empty());

        gameService.processBet(session, json);

        verify(betProcessor).process(json);
        verify(betProcessor).sendInvalidMessage(session);
    }

}

