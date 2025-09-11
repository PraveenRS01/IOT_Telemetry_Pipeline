package com.iot.telemetry.timeseries.controller;

import com.iot.telemetry.timeseries.models.UserEngagementSummary;
import com.iot.telemetry.timeseries.service.AnalyticsService;
import com.iot.telemetry.timeseries.service.InfluxDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/timeseries")
@CrossOrigin(origins = "*")
public class TimeSeriesController {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeSeriesController.class);
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private InfluxDBService influxDBService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Time Series Service is running!");
    }
    
    @GetMapping("/engagement/{userId}")
    public ResponseEntity<List<UserEngagementSummary>> getUserEngagement(@PathVariable String userId) {
        List<UserEngagementSummary> data = analyticsService.getUserEngagementData(userId);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/feature/{feature}")
    public ResponseEntity<List<UserEngagementSummary>> getFeatureEngagement(@PathVariable String feature) {
        List<UserEngagementSummary> data = analyticsService.getFeatureEngagementData(feature);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<UserEngagementSummary>> getRecentEngagement(@RequestParam(defaultValue = "10") int limit) {
        List<UserEngagementSummary> data = analyticsService.getRecentEngagementData(limit);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/analytics/summary")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRecords", analyticsService.getTotalRecords());
        summary.put("recentData", analyticsService.getRecentEngagementData(5));
        summary.put("status", "active");
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/processed-data")
    public ResponseEntity<Map<String, Object>> receiveProcessedData(@RequestBody Map<String, Object> data) {
        try {
            // Extract data from Map
            String userId = (String) data.get("userId");
            String sessionId = (String) data.get("sessionId");
            String feature = (String) data.get("feature");
            String action = (String) data.get("action");
            @SuppressWarnings("unchecked")
            Map<String, Object> metrics = (Map<String, Object>) data.get("metrics");
            Double engagementScore = ((Number) data.get("engagementScore")).doubleValue();
            
            // Store in InfluxDB
            influxDBService.writeTelemetryData(userId, sessionId, feature, action, metrics);
            
            // Store engagement metrics
            influxDBService.writeEngagementMetrics(userId, sessionId, feature, action, 
                                                 engagementScore, metrics);
            
            // Store in PostgreSQL
            analyticsService.storeEngagementSummary(userId, sessionId, feature, action, 
                                                  engagementScore, metrics);
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "Data stored successfully"));
        } catch (Exception e) {
            logger.error("Error storing processed data", e);
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
