package com.example.relieffeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RelieffeedApplication {
    public static void main(String[] args) {
        SpringApplication.run(RelieffeedApplication.class, args);
    }
}
