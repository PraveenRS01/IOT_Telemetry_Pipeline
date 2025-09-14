package com.iot.telemetry.ingestion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.telemetry.ingestion.models.TelemetryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${kafka.topic.telemetry:telemetry-data}")
    private String telemetryTopic;
    
    public void sendTelemetryData(TelemetryData data) {
        try {
            Map<String, Object> kafkaMessage = createKafkaMessage(data);
            String messageJson = objectMapper.writeValueAsString(kafkaMessage);
            
            String partitionKey = data.getUserId();
            
            // Send to Kafka
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
                telemetryTopic, 
                partitionKey, 
                messageJson
            );
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Successfully sent telemetry data to Kafka. Topic: {}, Partition: {}, Offset: {}", 
                              telemetryTopic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send telemetry data to Kafka", ex);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error sending telemetry data to Kafka", e);
        }
    }
    
    private Map<String, Object> createKafkaMessage(TelemetryData data) {
        Map<String, Object> message = new HashMap<>();
        message.put("id", data.getId());
        message.put("userId", data.getUserId());
        message.put("sessionId", data.getSessionId());
        message.put("feature", data.getFeature());
        message.put("action", data.getAction());
        message.put("timestamp", data.getTimestamp().toString());
        message.put("metrics", data.getMetricsJson());
        message.put("createdAt", data.getCreatedAt().toString());
        message.put("eventType", "TELEMETRY_DATA");
        message.put("source", "ingestion-service");
        
        return message;
    }
}