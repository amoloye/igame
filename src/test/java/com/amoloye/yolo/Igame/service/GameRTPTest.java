package com.amoloye.yolo.Igame.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameRTPTest {

    private static final int TOTAL_ROUNDS = 1_000_000;
    private static final int THREAD_COUNT = 24;
    private static final BigDecimal BET_AMOUNT = BigDecimal.ONE;
    private static final BigDecimal MULTIPLIER = BigDecimal.valueOf(9.9);

    @Test
    void simulateOneMillionRounds() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicReference<BigDecimal> totalBet = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> totalWon = new AtomicReference<>(BigDecimal.ZERO);

        int roundsPerThread = TOTAL_ROUNDS / THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                for (int j = 0; j < roundsPerThread; j++) {
                    int betNumber = ThreadLocalRandom.current().nextInt(1, 11);
                    int winningNumber = ThreadLocalRandom.current().nextInt(1, 11);

                    totalBet.getAndUpdate(current -> current.add(BET_AMOUNT));

                    if (betNumber == winningNumber) {
                        BigDecimal winnings = BET_AMOUNT.multiply(MULTIPLIER)
                                .setScale(2, RoundingMode.HALF_UP);
                        totalWon.getAndUpdate(current -> current.add(winnings));
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        BigDecimal rtp = totalWon.get()
                .divide(totalBet.get(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        System.out.printf("Total bet: $%s%n", totalBet.get());
        System.out.printf("Total won: $%s%n", totalWon.get());
        System.out.printf("RTP: %s%%%n", rtp);

        // Assert RTP close to theoretical value ~99%
        assertTrue(rtp.compareTo(BigDecimal.valueOf(98)) > 0, "RTP should be > 98%");
        assertTrue(rtp.compareTo(BigDecimal.valueOf(100)) <= 0, "RTP should be <= 100%");
    }
}
