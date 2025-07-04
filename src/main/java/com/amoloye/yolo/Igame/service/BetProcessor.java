package com.amoloye.yolo.Igame.service;

import com.amoloye.yolo.Igame.requestDto.Bet;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
public class BetProcessor {

    private final ObjectMapper mapper;
    private final Validator validator;

    public BetProcessor(ObjectMapper mapper, Validator validator) {
        this.mapper = mapper;
        this.validator = validator;
    }

    public Optional<Bet> process(String json) {
        try {
            Bet bet = mapper.readValue(json, Bet.class);
            Set<ConstraintViolation<Bet>> violations = validator.validate(bet);
            return violations.isEmpty() ? Optional.of(bet) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void sendInvalidMessage(WebSocketSession session) {
        sendMessage(session, "Invalid bet.");
    }

    public void sendMessage(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            // log or ignore
        }
    }
}
