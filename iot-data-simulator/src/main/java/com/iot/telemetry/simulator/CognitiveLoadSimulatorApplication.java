package com.iot.telemetry.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CognitiveLoadSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CognitiveLoadSimulatorApplication.class, args);
    }
}