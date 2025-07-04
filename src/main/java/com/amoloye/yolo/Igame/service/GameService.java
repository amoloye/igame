package com.amoloye.yolo.Igame.service;


import com.amoloye.yolo.Igame.responseDto.PlayerBet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import java.util.*;
import java.util.concurrent.*;


@Slf4j
@Service
public class GameService {

    private final Map<WebSocketSession, String> playerMap = new ConcurrentHashMap<>();
    private final Set<String> nicknameSet = ConcurrentHashMap.newKeySet(); // Unique nicknames per round
    private final CopyOnWriteArrayList<PlayerBet> currentBets = new CopyOnWriteArrayList<>();

    private final BetProcessor betProcessor;
    private final RoundManager roundManager;

    public GameService(BetProcessor betProcessor, RoundManager roundManager) {
        this.betProcessor = betProcessor;
        this.roundManager = roundManager;
    }

    /**
     * Adds a new player connection.
     */
    public void addPlayer(WebSocketSession session) {
        playerMap.put(session, session.getId());
        log.info("Player connected: {}", session.getId());
    }

    /**
     * Removes a disconnected player and cleans up their nickname.
     */
    public void removePlayer(WebSocketSession session) {
        String sessionId = playerMap.remove(session);
        // Also remove any bet and nickname associated with the session
        currentBets.removeIf(pb -> {
            boolean match = pb.session().equals(session);
            if (match) {
                nicknameSet.remove(pb.bet().nickname().toLowerCase());
            }
            return match;
        });
        log.info("Player disconnected: {}", sessionId);
    }

    /**
     * Processes an incoming bet message.
     */
    public void processBet(WebSocketSession session, String json) {
        try {
            betProcessor.process(json).ifPresentOrElse(
                    bet -> {
                        String nicknameLower = bet.nickname().toLowerCase();

                        // Global nickname check
                        boolean nicknameTaken = nicknameSet.contains(nicknameLower);
                        if (nicknameTaken) {
                            betProcessor.sendMessage(session, "Nickname already used this round. Please choose another.");
                            log.warn("Duplicate nickname '{}' attempted by session {}", bet.nickname(), session.getId());
                            return;
                        }

                        // Accept bet
                        currentBets.add(new PlayerBet(session, bet));
                        nicknameSet.add(nicknameLower);
                        log.info("Bet accepted from session {} with nickname {}", session.getId(), bet.nickname());
                    },
                    () -> {
                        betProcessor.sendInvalidMessage(session);
                        log.warn("Invalid bet received from session {}", session.getId());
                    }
            );
        } catch (Exception e) {
            log.error("Unexpected error processing bet for session {}", session.getId(), e);
            betProcessor.sendMessage(session, "Internal server error while processing your bet.");
        }
    }

    /**
     * Starts a game round every 30 seconds.
     */
    @Scheduled(fixedDelay = 30_000)
    private void startRound() {
        log.info("====== ROUND START ======");

        if (playerMap.isEmpty()) {
            log.debug("No players connected. Skipping round.");
            return;
        }

        try {
            roundManager.executeRound(currentBets, playerMap.keySet());
        } catch (Exception e) {
            log.error("Error during round execution", e);
        } finally {
            currentBets.clear();
            nicknameSet.clear(); // reset for next round
        }

        log.info("====== ROUND END ======");
    }
}
