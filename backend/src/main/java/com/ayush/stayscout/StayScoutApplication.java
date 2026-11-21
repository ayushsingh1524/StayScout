package com.ayush.stayscout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StayScoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(StayScoutApplication.class, args);
    }
}
