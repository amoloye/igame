package com.amoloye.yolo.Igame.service;

import com.amoloye.yolo.Igame.requestDto.Bet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BetProcessorTest {

    private BetProcessor betProcessor;
    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        betProcessor = new BetProcessor(objectMapper, validator);
    }

    @Test
    void testProcessValidBet() throws JsonProcessingException {
        Bet validBet = new Bet("Ade", 5, BigDecimal.TEN);
        String json = objectMapper.writeValueAsString(validBet);

        Optional<Bet> bet = betProcessor.process(json);

        assertTrue(bet.isPresent());
        assertEquals("Ade", bet.get().nickname());
        assertEquals(5, bet.get().number());
        assertEquals(10, bet.get().amount().intValue());
    }

    @Test
    void testProcessInvalidBet() throws JsonProcessingException {
        // number 15 is invalid because allowed range is 1-10
        Bet invalidBet = new Bet("Anu", 15, BigDecimal.TEN);
        String json = objectMapper.writeValueAsString(invalidBet);

        Optional<Bet> bet = betProcessor.process(json);

        assertTrue(bet.isEmpty());
    }
}

