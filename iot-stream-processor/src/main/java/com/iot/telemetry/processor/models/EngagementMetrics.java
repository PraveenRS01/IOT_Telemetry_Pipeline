package com.iot.telemetry.processor.models;

import java.time.LocalDateTime;
import java.util.Map;

public class EngagementMetrics {
    private String userId;
    private String sessionId;
    private String feature;
    private double engagementScore;
    private double averageResponseTime;
    private int totalClicks;
    private int errorCount;
    private double sessionDuration;
    private LocalDateTime timestamp;
    private Map<String, Object> additionalMetrics;

    public EngagementMetrics() {}

    public EngagementMetrics(String userId, String sessionId, String feature, 
                           double engagementScore, double averageResponseTime, 
                           int totalClicks, int errorCount, double sessionDuration) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.feature = feature;
        this.engagementScore = engagementScore;
        this.averageResponseTime = averageResponseTime;
        this.totalClicks = totalClicks;
        this.errorCount = errorCount;
        this.sessionDuration = sessionDuration;
        this.timestamp = LocalDateTime.now();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }

    public double getEngagementScore() { return engagementScore; }
    public void setEngagementScore(double engagementScore) { this.engagementScore = engagementScore; }

    public double getAverageResponseTime() { return averageResponseTime; }
    public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }

    public int getTotalClicks() { return totalClicks; }
    public void setTotalClicks(int totalClicks) { this.totalClicks = totalClicks; }

    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }

    public double getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(double sessionDuration) { this.sessionDuration = sessionDuration; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }
    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; }
}