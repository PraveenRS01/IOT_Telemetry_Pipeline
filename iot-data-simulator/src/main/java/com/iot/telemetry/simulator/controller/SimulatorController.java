package com.iot.telemetry.simulator.controller;

import com.iot.telemetry.simulator.models.UserBehaviorData;
import com.iot.telemetry.simulator.service.TelemetryHttpService;
import com.iot.telemetry.simulator.service.UserBehaviorSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/simulator")
public class SimulatorController {
    
    @Autowired
    private UserBehaviorSimulator simulator;
    
    @Autowired
    private TelemetryHttpService telemetryHttpService;
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Simulator is running!");
    }
    
    @GetMapping("/generate")
    public ResponseEntity<UserBehaviorData> generateData() {
        return ResponseEntity.ok(simulator.generateUserBehavior());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("status", "running");
        stats.put("generationInterval", "5 seconds");
        stats.put("timestamp", System.currentTimeMillis());
        
        // Add HTTP service statistics
        Map<String, Object> httpStats = telemetryHttpService.getStatistics();
        stats.putAll(httpStats);
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/start")
    public ResponseEntity<String> startGeneration() {
        return ResponseEntity.ok("Data generation started!");
    }
    
    @PostMapping("/stop")
    public ResponseEntity<String> stopGeneration() {
        return ResponseEntity.ok("Data generation stopped!");
    }
}