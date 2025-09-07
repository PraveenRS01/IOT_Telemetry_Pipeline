package com.iot.telemetry.ingestion.controller;

import com.iot.telemetry.ingestion.models.TelemetryData;
import com.iot.telemetry.ingestion.service.TelemetryIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/telemetry")
@CrossOrigin(origins = "*")
public class TelemetryController {
    
    private static final Logger logger = LoggerFactory.getLogger(TelemetryController.class);
    
    @Autowired
    private TelemetryIngestionService telemetryIngestionService;
    
    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingestTelemetryData(@Valid @RequestBody TelemetryRequest request) {
        try {
            logger.info("Received telemetry data: {}", request);
            
            TelemetryData savedData = telemetryIngestionService.ingestTelemetryData(
                request.getUserId(),
                request.getSessionId(),
                request.getFeature(),
                request.getAction(),
                request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now(),
                request.getMetrics()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Telemetry data ingested successfully");
            response.put("dataId", savedData.getId());
            response.put("timestamp", savedData.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error ingesting telemetry data", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to ingest telemetry data: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TelemetryData>> getTelemetryByUser(@PathVariable String userId) {
        List<TelemetryData> data = telemetryIngestionService.getTelemetryByUserId(userId);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<TelemetryData>> getTelemetryBySession(@PathVariable String sessionId) {
        List<TelemetryData> data = telemetryIngestionService.getTelemetryBySessionId(sessionId);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/feature/{feature}")
    public ResponseEntity<List<TelemetryData>> getTelemetryByFeature(@PathVariable String feature) {
        List<TelemetryData> data = telemetryIngestionService.getTelemetryByFeature(feature);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTelemetryStats() {
        List<TelemetryData> allData = telemetryIngestionService.getAllTelemetry();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", allData.size());
        stats.put("uniqueUsers", allData.stream().map(TelemetryData::getUserId).distinct().count());
        stats.put("uniqueSessions", allData.stream().map(TelemetryData::getSessionId).distinct().count());
        stats.put("uniqueFeatures", allData.stream().map(TelemetryData::getFeature).distinct().count());
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "iot-ingestion-service");
        health.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
    
    // Request DTO for validation
    public static class TelemetryRequest {
        private String userId;
        private String sessionId;
        private String feature;
        private String action;
        private LocalDateTime timestamp;
        private Map<String, Object> metrics;
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getFeature() { return feature; }
        public void setFeature(String feature) { this.feature = feature; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    }
}