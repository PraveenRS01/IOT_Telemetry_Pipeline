package com.iot.telemetry.processor.controller;

import com.iot.telemetry.processor.service.TelemetryConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stream-processor")
public class StreamProcessorController {
    
    @Autowired
    private TelemetryConsumerService consumerService;
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Stream Processor is running!");
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(consumerService.getStatistics());
    }
    
    @GetMapping("/engagement/{userId}")
    public ResponseEntity<Map<String, Object>> getEngagementMetrics(@PathVariable String userId) {
        return ResponseEntity.ok(consumerService.getEngagementMetrics(userId));
    }
    
    @GetMapping("/anomalies/{userId}")
    public ResponseEntity<Map<String, Object>> getAnomalyEvents(@PathVariable String userId) {
        return ResponseEntity.ok(consumerService.getAnomalyEvents(userId));
    }
    
    @GetMapping("/features/engagement")
    public ResponseEntity<Map<String, Object>> getFeatureEngagementScores() {
        Map<String, Object> stats = consumerService.getStatistics();
        return ResponseEntity.ok(Map.of(
            "featureEngagementScores", stats.get("featureEngagementScores"),
            "featureErrorRates", stats.get("featureErrorRates")
        ));
    }

    @PostMapping("/test-anomaly")
    public ResponseEntity<Map<String, Object>> testAnomaly() {
        // Simulate high response time data to trigger anomaly
        Map<String, Object> testData = Map.of(
            "userId", "test-user",
            "sessionId", "test-session",
            "feature", "test-feature",
            "action", "test-action",
            "metrics", Map.of(
                "responseTime", 6.0,  // This should trigger HIGH_RESPONSE_TIME anomaly
                "errorCount", 5,      // This should trigger HIGH_ERROR_RATE anomaly
                "clickCount", 25,     // This should trigger UNUSUAL_CLICK_PATTERN anomaly
                "sessionDuration", 10.0
            )
        );
        
        consumerService.processData(testData);
        
        return ResponseEntity.ok(Map.of(
            "message", "Test anomaly data processed",
            "checkStats", "Use /stats endpoint to see anomaly count"
        ));
    }
}