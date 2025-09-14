package com.iot.telemetry.timeseries.models;

import java.util.Map;

public class ProcessedTelemetryData {
    private String userId;
    private String sessionId;
    private String feature;
    private String action;
    private Map<String, Object> metrics;
    private double engagementScore;
    private double responseTime;
    private int clickCount;
    private int errorCount;
    private double sessionDuration;
    
    public ProcessedTelemetryData() {}
    
    public ProcessedTelemetryData(String userId, String sessionId, String feature, String action,
                                Map<String, Object> metrics, double engagementScore,
                                double responseTime, int clickCount, int errorCount, double sessionDuration) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.feature = feature;
        this.action = action;
        this.metrics = metrics;
        this.engagementScore = engagementScore;
        this.responseTime = responseTime;
        this.clickCount = clickCount;
        this.errorCount = errorCount;
        this.sessionDuration = sessionDuration;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getFeature() {
        return feature;
    }
    
    public void setFeature(String feature) {
        this.feature = feature;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Map<String, Object> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
    
    public double getEngagementScore() {
        return engagementScore;
    }
    
    public void setEngagementScore(double engagementScore) {
        this.engagementScore = engagementScore;
    }
    
    public double getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }
    
    public int getClickCount() {
        return clickCount;
    }
    
    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
    
    public double getSessionDuration() {
        return sessionDuration;
    }
    
    public void setSessionDuration(double sessionDuration) {
        this.sessionDuration = sessionDuration;
    }
    
    @Override
    public String toString() {
        return "ProcessedTelemetryData{" +
                "userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", feature='" + feature + '\'' +
                ", action='" + action + '\'' +
                ", engagementScore=" + engagementScore +
                ", responseTime=" + responseTime +
                ", clickCount=" + clickCount +
                ", errorCount=" + errorCount +
                ", sessionDuration=" + sessionDuration +
                '}';
    }
}
