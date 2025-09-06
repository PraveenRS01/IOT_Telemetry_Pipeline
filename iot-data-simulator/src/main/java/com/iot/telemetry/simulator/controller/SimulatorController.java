package com.iot.telemetry.simulator.controller;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iot.telemetry.simulator.models.UserBehaviorData;
import com.iot.telemetry.simulator.service.UserBehaviorSimulator;

@RestController
@RequestMapping("/api/v1/simulator")
public class SimulatorController {
    
    @Autowired
    private UserBehaviorSimulator simulator;
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Simulator is running!");
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("status", "running");
        stats.put("generationInterval", "5 seconds");
        stats.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/generate")
    public ResponseEntity<UserBehaviorData> generateData() {
        return ResponseEntity.ok(simulator.generateUserBehavior());
    }

    @PostMapping("/start")
    public ResponseEntity<String> startGeneration() {
        // This will start the scheduled task
        return ResponseEntity.ok("Data generation started!");
    }
}