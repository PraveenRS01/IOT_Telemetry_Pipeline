package com.iot.telemetry.ingestion.service;

import com.iot.telemetry.ingestion.models.TelemetryData;
import com.iot.telemetry.ingestion.repository.TelemetryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TelemetryIngestionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TelemetryIngestionService.class);
    
    @Autowired
    private TelemetryRepository telemetryRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    public TelemetryData ingestTelemetryData(String userId, String sessionId, String feature, 
                                           String action, LocalDateTime timestamp, Map<String, Object> metrics) {
        
        try {                                    
            logger.info("Ingesting telemetry data for user: {}, session: {}, feature: {}, action: {}", 
                    userId, sessionId, feature, action);
            
            // Create new telemetry data entity
            TelemetryData telemetryData = new TelemetryData(userId, sessionId, feature, action, timestamp);
            
            // Convert metrics map to JSON string (simple implementation)
            if (metrics != null && !metrics.isEmpty()) {
                telemetryData.setMetricsJson(convertMetricsToJson(metrics));
            }
            
            // Save to database
            TelemetryData savedData = telemetryRepository.save(telemetryData);
            logger.info("Telemetry data saved to database with ID: {}", savedData.getId());
            
            // send data to Kafka for stream processing
            kafkaProducerService.sendTelemetryData(savedData);
            logger.info("Telemetry data sent to Kafka for stream processing");
            
            return savedData;
        } catch (Exception e) {
            logger.error("Error ingesting telemetry data", e);
            throw new RuntimeException("Failed to ingest telemetry data", e);
        }
    }
    
    public List<TelemetryData> getTelemetryByUserId(String userId) {
        return telemetryRepository.findByUserId(userId);
    }
    
    public List<TelemetryData> getTelemetryBySessionId(String sessionId) {
        return telemetryRepository.findBySessionId(sessionId);
    }
    
    public List<TelemetryData> getTelemetryByFeature(String feature) {
        return telemetryRepository.findByFeature(feature);
    }
    
    public List<TelemetryData> getTelemetryByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return telemetryRepository.findByTimestampBetween(startTime, endTime);
    }
    
    public Long getTelemetryCountByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return telemetryRepository.countByTimestampBetween(startTime, endTime);
    }
    
    public List<TelemetryData> getAllTelemetry() {
        return telemetryRepository.findAll();
    }
    
    private String convertMetricsToJson(Map<String, Object> metrics) {
        // Simple JSON conversion - in production, use Jackson or Gson
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}