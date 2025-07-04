package com.amoloye.yolo.Igame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IgameApplication {

    public static void main(String[] args) {
        SpringApplication.run(IgameApplication.class, args);
    }

}

