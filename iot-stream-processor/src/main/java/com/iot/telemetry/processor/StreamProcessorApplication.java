package com.iot.telemetry.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class StreamProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamProcessorApplication.class, args);
    }
}
