package com.amoloye.yolo.Igame.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

public record Bet(
    @NotBlank
    String nickname,
    @Range(min = 1, max = 10)
    int number,
    @Positive
    BigDecimal amount
) {}
