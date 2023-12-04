package com.example.demo;

import io.firetail.logging.spring.EnableFiretail;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@SpringBootApplication
@RestController
@EnableFiretail
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return String.format("Hello %s, utc: %s!", LocalDateTime.now(), ZonedDateTime.now(Clock.systemUTC()));
    }
}
