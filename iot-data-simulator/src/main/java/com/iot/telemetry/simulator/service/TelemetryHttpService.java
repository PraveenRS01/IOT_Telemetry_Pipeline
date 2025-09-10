package com.iot.telemetry.simulator.service;

import com.iot.telemetry.simulator.models.UserBehaviorData;

import reactor.util.retry.Retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TelemetryHttpService {
    
    private static final Logger logger = LoggerFactory.getLogger(TelemetryHttpService.class);
    
    @Autowired
    private WebClient webClient;
    
    @Value("${simulator.ingestion.url:http://localhost:8082}")
    private String ingestionServiceUrl;

    @Value("${simulator.retry.maxAttempts:3}")
    private int maxRetryAttempts;
    
    @Value("${simulator.retry.delay:1000}")
    private long retryDelayMs;

    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    
    public void sendTelemetryData(UserBehaviorData data) {
        try {
            logger.info("Sending telemetry data to ingestion service: {}", data);
            
            // Convert UserBehaviorData to the format expected by ingestion service
            Map<String, Object> requestBody = createIngestionRequest(data);
            
            webClient.post()
            .uri(ingestionServiceUrl + "/api/v1/telemetry/ingest")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Map.class)
            .timeout(Duration.ofSeconds(10))
            .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofMillis(retryDelayMs))
                .filter(throwable -> {
                    if (throwable instanceof WebClientResponseException) {
                        WebClientResponseException ex = (WebClientResponseException) throwable;
                        return ex.getStatusCode().is5xxServerError();
                    }
                    return true; // Retry on other exceptions
                })
                .doBeforeRetry(retrySignal -> {
                    logger.warn("Retrying request. Attempt: {}, Error: {}", 
                               retrySignal.totalRetries() + 1, retrySignal.failure().getMessage());
                }))
            .doOnSuccess(response -> {
                successCount.incrementAndGet();
                logger.info("Successfully sent telemetry data. Response: {}", response);
            })
            .doOnError(error -> {
                failureCount.incrementAndGet();
                if (error instanceof WebClientResponseException) {
                    WebClientResponseException ex = (WebClientResponseException) error;
                    logger.error("Failed to send telemetry data after {} retries. Status: {}, Response: {}", 
                               maxRetryAttempts, ex.getStatusCode(), ex.getResponseBodyAsString());
                } else {
                    logger.error("Failed to send telemetry data after {} retries", maxRetryAttempts, error);
                }
            })
            .block();
                
        } catch (Exception e) {
            failureCount.incrementAndGet();
            logger.error("Error sending telemetry data", e);
        }
    }
    
    private Map<String, Object> createIngestionRequest(UserBehaviorData data) {
        return Map.of(
            "userId", data.getUserId(),
            "sessionId", data.getSessionId(),
            "feature", data.getFeature(),
            "action", data.getAction(),
            "timestamp", data.getTimestamp().toString(),
            "metrics", data.getMetrics() != null ? data.getMetrics() : Map.of()
        );
    }

    public Map<String, Object> getStatistics() {
        return Map.of(
            "successCount", successCount.get(),
            "failureCount", failureCount.get(),
            "totalAttempts", successCount.get() + failureCount.get(),
            "successRate", successCount.get() + failureCount.get() > 0 ? 
                (double) successCount.get() / (successCount.get() + failureCount.get()) : 0.0
        );
    }
}