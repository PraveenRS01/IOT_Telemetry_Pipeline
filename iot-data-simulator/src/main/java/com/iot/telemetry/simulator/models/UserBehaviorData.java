package com.iot.telemetry.simulator.models;

import java.time.Instant;
import java.util.Map;

public class UserBehaviorData {
    private String userId;
    private String sessionId;
    private String feature;
    private String action;
    private Instant timestamp;
    private Map<String, Object> metrics;
    
    // Constructors
    public UserBehaviorData() {}
    
    public UserBehaviorData(String userId, String sessionId, String feature, String action) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.feature = feature;
        this.action = action;
        this.timestamp = Instant.now();
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    
    @Override
    public String toString() {
        return "UserBehaviorData{" +
                "userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", feature='" + feature + '\'' +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                ", metrics=" + metrics +
                '}';
    }
}