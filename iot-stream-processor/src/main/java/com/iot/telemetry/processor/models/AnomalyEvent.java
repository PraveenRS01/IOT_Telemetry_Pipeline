package com.iot.telemetry.processor.models;

import java.time.LocalDateTime;
import java.util.Map;

public class AnomalyEvent {
    private String userId;
    private String sessionId;
    private String feature;
    private String anomalyType;
    private String severity;
    private String description;
    private double threshold;
    private double actualValue;
    private LocalDateTime timestamp;
    private Map<String, Object> context;

    public AnomalyEvent() {}

    public AnomalyEvent(String userId, String sessionId, String feature, 
                       String anomalyType, String severity, String description,
                       double threshold, double actualValue) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.feature = feature;
        this.anomalyType = anomalyType;
        this.severity = severity;
        this.description = description;
        this.threshold = threshold;
        this.actualValue = actualValue;
        this.timestamp = LocalDateTime.now();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }

    public String getAnomalyType() { return anomalyType; }
    public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public double getActualValue() { return actualValue; }
    public void setActualValue(double actualValue) { this.actualValue = actualValue; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}