package com.iot.telemetry.processor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.telemetry.processor.models.AnomalyEvent;
import com.iot.telemetry.processor.models.EngagementMetrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TelemetryConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(TelemetryConsumerService.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private WebClient webClient;
    
    private final AtomicLong processedCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    private final AtomicLong anomalyCount = new AtomicLong(0);

    // In-memory storage for real-time processing
    private final Map<String, List<EngagementMetrics>> userEngagement = new ConcurrentHashMap<>();
    private final Map<String, List<AnomalyEvent>> userAnomalies = new ConcurrentHashMap<>();
    
    // Real-time metrics
    private final Map<String, Double> featureEngagementScores = new ConcurrentHashMap<>();
    private final Map<String, Integer> featureErrorRates = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    @KafkaListener(topics = "${kafka.topic.telemetry:telemetry-data}", groupId = "stream-processor")
    public void processTelemetryData(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        try {
            logger.info("Received telemetry data from Kafka. Topic: {}, Partition: {}, Offset: {}", 
                      topic, partition, offset);
            
            // Parse the message
            Map<String, Object> telemetryData = objectMapper.readValue(message, Map.class);
            
            processData(telemetryData);
            
            processedCount.incrementAndGet();
            logger.info("Successfully processed telemetry data. Processed count: {}", processedCount.get());
            
        } catch (Exception e) {
            errorCount.incrementAndGet();
            logger.error("Error processing telemetry data from Kafka", e);
        }
    }

    private void writeToTimeSeriesDB(String userId, String sessionId, String feature, String action, 
                                    Map<String, Object> metrics, EngagementMetrics engagement) {
        try {
            // Create processed data object
            Map<String, Object> processedData = new HashMap<>();
            processedData.put("userId", userId);
            processedData.put("sessionId", sessionId);
            processedData.put("feature", feature);
            processedData.put("action", action);
            processedData.put("metrics", metrics);
            processedData.put("engagementScore", engagement != null ? engagement.getEngagementScore() : 0.0);
            processedData.put("responseTime", getDoubleValue(metrics, "responseTime", 0.0));
            processedData.put("clickCount", getIntValue(metrics, "clickCount", 0));
            processedData.put("errorCount", getIntValue(metrics, "errorCount", 0));
            processedData.put("sessionDuration", getDoubleValue(metrics, "sessionDuration", 0.0));
            
            // Send to time series service
            webClient.post()
                .uri("http://localhost:8084/api/v1/timeseries/processed-data")
                .bodyValue(processedData)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(
                    response -> logger.info("Successfully sent processed data to time series service: {}", response),
                    error -> logger.error("Failed to send processed data to time series service", error)
                );
                
        } catch (Exception e) {
            logger.error("Error sending processed data to time series service", e);
        }
    }
    
    public void processData(Map<String, Object> data) {
        String userId = (String) data.get("userId");
        String sessionId = (String) data.get("sessionId");
        String feature = (String) data.get("feature");
        String action = (String) data.get("action");
        Object metricsObj = data.get("metrics");
        
        logger.info("Processing data for user: {}, feature: {}, action: {}", userId, feature, action);
        
        Map<String, Object> metrics = parseMetrics(metricsObj);
        if (metrics == null) {
            logger.warn("Failed to parse metrics for user: {}, feature: {}", userId, feature);
            return;
        }

        // 1. Calculate Engagement Metrics
        EngagementMetrics engagement = calculateEngagementMetrics(userId, sessionId, feature, action, metrics);
        if (engagement != null) {
            storeEngagementMetrics(engagement);
            updateFeatureEngagement(feature, engagement.getEngagementScore());
        }
        
        // 2. Detect Anomalies
        List<AnomalyEvent> anomalies = detectAnomalies(userId, sessionId, feature, action, metricsObj);
        for (AnomalyEvent anomaly : anomalies) {
            storeAnomalyEvent(anomaly);
            updateFeatureErrorRate(feature);
            logger.warn("ANOMALY DETECTED: {} - {}", anomaly.getAnomalyType(), anomaly.getDescription());
        }
        
        // 3. Send processed data to Time Series Service
        writeToTimeSeriesDB(userId, sessionId, feature, action, metrics, engagement);
        
        // 4. Real-time Dashboard Updates
        updateRealTimeDashboards(userId, feature, engagement, anomalies);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseMetrics(Object metricsObj) {
        try {
            if (metricsObj instanceof Map) {
                // Already a Map, return as is
                return (Map<String, Object>) metricsObj;
            } else if (metricsObj instanceof String) {
                // JSON string, parse it
                String metricsJson = (String) metricsObj;
                logger.debug("Parsing metrics JSON: {}", metricsJson);
                return objectMapper.readValue(metricsJson, Map.class);
            } else {
                logger.warn("Unknown metrics type: {}", metricsObj != null ? metricsObj.getClass() : "null");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error parsing metrics: {}", metricsObj, e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private EngagementMetrics calculateEngagementMetrics(String userId, String sessionId, String feature, 
                                                       String action, Object metricsObj) {
        if (metricsObj instanceof Map) {
            Map<String, Object> metrics = (Map<String, Object>) metricsObj;
            
            // Extract metrics
            double responseTime = getDoubleValue(metrics, "responseTime", 0.0);
            int errorCount = getIntValue(metrics, "errorCount", 0);
            int clickCount = getIntValue(metrics, "clickCount", 1);
            double sessionDuration = getDoubleValue(metrics, "sessionDuration", 0.0);
            
            // Calculate engagement score (0-100)
            double engagementScore = calculateEngagementScore(responseTime, errorCount, clickCount, sessionDuration);
            
            return new EngagementMetrics(userId, sessionId, feature, engagementScore, 
                                      responseTime, clickCount, errorCount, sessionDuration);
        }
        return null;
    }
    
    private double calculateEngagementScore(double responseTime, int errorCount, int clickCount, double sessionDuration) {
        double score = 100.0;
        
        // Convert response time from milliseconds to seconds for calculation
        double responseTimeSeconds = responseTime / 1000.0;
        
        // Penalize high response times (now in seconds)
        if (responseTimeSeconds > 2.0) score -= (responseTimeSeconds - 2.0) * 10;
        
        // Penalize errors
        score -= errorCount * 15;
        
        // Reward high click count (engagement)
        score += Math.min(clickCount * 2, 20);
        
        // Reward longer sessions (sessionDuration is already in seconds)
        score += Math.min(sessionDuration / 60.0 * 5, 15);
        
        return Math.max(0, Math.min(100, score));
    }
    
    @SuppressWarnings("unchecked")
    private List<AnomalyEvent> detectAnomalies(String userId, String sessionId, String feature, 
                                            String action, Object metricsObj) {
        List<AnomalyEvent> anomalies = new ArrayList<>();
        
        if (metricsObj instanceof Map) {
            Map<String, Object> metrics = (Map<String, Object>) metricsObj;
            
            // 1. High Response Time Anomaly (convert to seconds)
            double responseTime = getDoubleValue(metrics, "responseTime", 0.0);
            double responseTimeSeconds = responseTime / 1000.0; // Convert to seconds
            if (responseTimeSeconds > 5.0) {
                anomalies.add(new AnomalyEvent(userId, sessionId, feature, "HIGH_RESPONSE_TIME", 
                                            "WARNING", "Response time exceeds 5 seconds", 5.0, responseTimeSeconds));
            }
            
            // 2. High Error Rate Anomaly
            int errorCount = getIntValue(metrics, "errorCount", 0);
            if (errorCount > 3) {
                anomalies.add(new AnomalyEvent(userId, sessionId, feature, "HIGH_ERROR_RATE", 
                                            "ERROR", "Error count exceeds 3", 3, errorCount));
            }
            
            // 3. Low Engagement Anomaly (use corrected calculation)
            double engagementScore = calculateEngagementScore(responseTime, errorCount, 
                                                        getIntValue(metrics, "clickCount", 1), 
                                                        getDoubleValue(metrics, "sessionDuration", 0.0));
            if (engagementScore < 30) {
                anomalies.add(new AnomalyEvent(userId, sessionId, feature, "LOW_ENGAGEMENT", 
                                            "WARNING", "User engagement is very low", 30.0, engagementScore));
            }
            
            // 4. Unusual Click Pattern Anomaly
            int clickCount = getIntValue(metrics, "clickCount", 0);
            if (clickCount > 20) {
                anomalies.add(new AnomalyEvent(userId, sessionId, feature, "UNUSUAL_CLICK_PATTERN", 
                                            "INFO", "Unusually high click count detected", 20, clickCount));
            }
        }
        
        return anomalies;
    }
    
    private void updateRealTimeDashboards(String userId, String feature, EngagementMetrics engagement, 
                                        List<AnomalyEvent> anomalies) {

        // Simulate real-time dashboard updates
        logger.info("Updating real-time dashboards for user: {}, feature: {}", userId, feature);
        
        if (engagement != null) {
            logger.info("Dashboard Update - User: {}, Feature: {}, Engagement Score: {}", 
                       userId, feature, engagement.getEngagementScore());
        }
        
        for (AnomalyEvent anomaly : anomalies) {
            logger.info("Dashboard Alert - Anomaly: {} for user: {} in feature: {}", 
                       anomaly.getAnomalyType(), userId, feature);
        }
    }
    
    // Helper methods
    private double getDoubleValue(Map<String, Object> metrics, String key, double defaultValue) {
        Object value = metrics.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
    
    private int getIntValue(Map<String, Object> metrics, String key, int defaultValue) {
        Object value = metrics.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
    
    private void storeEngagementMetrics(EngagementMetrics engagement) {
        String key = engagement.getUserId() + "_" + engagement.getSessionId();
        userEngagement.computeIfAbsent(key, k -> new ArrayList<>()).add(engagement);
    }
    
    private void storeAnomalyEvent(AnomalyEvent anomaly) {
        String key = anomaly.getUserId() + "_" + anomaly.getSessionId();
        userAnomalies.computeIfAbsent(key, k -> new ArrayList<>()).add(anomaly);
        anomalyCount.incrementAndGet();
    }
    
    private void updateFeatureEngagement(String feature, double engagementScore) {
        featureEngagementScores.compute(feature, (key, existingScore) -> {
            if (existingScore == null) {
                return engagementScore;
            } else {
                return (existingScore + engagementScore) / 2.0;
            }
        });
    }
    
    private void updateFeatureErrorRate(String feature) {
        featureErrorRates.merge(feature, 1, Integer::sum);
    }
    
    public Map<String, Object> getStatistics() {
        return Map.of(
            "processedCount", processedCount.get(),
            "errorCount", errorCount.get(),
            "anomalyCount", anomalyCount.get(),
            "totalReceived", processedCount.get() + errorCount.get(),
            "successRate", processedCount.get() + errorCount.get() > 0 ? 
                (double) processedCount.get() / (processedCount.get() + errorCount.get()) : 0.0,
            "featureEngagementScores", new HashMap<>(featureEngagementScores),
            "featureErrorRates", new HashMap<>(featureErrorRates)
        );
    }
    
    // Additional API endpoints for real-time data
    public Map<String, Object> getEngagementMetrics(String userId) {
        return userEngagement.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(userId + "_"))
            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }
    
    public Map<String, Object> getAnomalyEvents(String userId) {
        return userAnomalies.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(userId + "_"))
            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }
}