package com.amoloye.yolo.Igame.service;

import com.amoloye.yolo.Igame.requestDto.Bet;
import com.amoloye.yolo.Igame.responseDto.Winner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.amoloye.yolo.Igame.responseDto.PlayerBet;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;



@Component
public class RoundManager {

    private final ObjectMapper mapper;
    private final Random random = new SecureRandom();

    public RoundManager(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void executeRound(List<PlayerBet> bets, Set<WebSocketSession> allPlayers) {
        int winningNumber = random.nextInt(1, 11); // 1 to 10 inclusive
        List<Winner> winners = new ArrayList<>();

        for (PlayerBet playerBet : bets) {
            Bet bet = playerBet.bet();
            WebSocketSession session = playerBet.session();

            if (bet.number() == winningNumber) {
                BigDecimal winnings = bet.amount().multiply(BigDecimal.valueOf(9.9));
                winners.add(new Winner(bet.nickname(), winnings));
                send(session, "WIN: " + winnings);
            } else {
                send(session, "LOSS");
            }
        }

        broadcast(allPlayers, winners);
    }

    private void send(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (Exception ignored) {}
    }

    private void broadcast(Set<WebSocketSession> sessions, List<Winner> winners) {
        try {
            String summary = mapper.writeValueAsString(winners);
            for (WebSocketSession session : sessions) {
                send(session, "Round Result: " + summary);
            }
        } catch (Exception ignored) {}
    }
}
