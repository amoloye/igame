package com.amoloye.yolo.Igame.responseDto;

import com.amoloye.yolo.Igame.requestDto.Bet;
import org.springframework.web.socket.WebSocketSession;

public record PlayerBet(WebSocketSession session, Bet bet) {
}
