package com.iot.telemetry.timeseries.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_engagement_summary")
public class UserEngagementSummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @Column(name = "feature", nullable = false)
    private String feature;
    
    @Column(name = "action", nullable = false)
    private String action;
    
    // Core engagement metrics
    @Column(name = "engagement_score", nullable = false)
    private Double engagementScore;
    
    @Column(name = "response_time", nullable = false)
    private Double responseTime;
    
    @Column(name = "click_count", nullable = false)
    private Integer clickCount;
    
    @Column(name = "error_count", nullable = false)
    private Integer errorCount;
    
    @Column(name = "session_duration", nullable = false)
    private Double sessionDuration;
    
    // Additional realistic metrics
    @Column(name = "time_between_clicks")
    private Integer timeBetweenClicks;
    
    @Column(name = "error_rate")
    private Double errorRate;
    
    @Column(name = "usage_frequency")
    private Integer usageFrequency;
    
    @Column(name = "page_load_time")
    private Double pageLoadTime;
    
    @Column(name = "scroll_depth")
    private Double scrollDepth;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserEngagementSummary() {}
    
    public UserEngagementSummary(String userId, String sessionId, String feature, String action,
                               Double engagementScore, Double responseTime, Integer clickCount, 
                               Integer errorCount, Double sessionDuration, Integer timeBetweenClicks,
                               Double errorRate, Integer usageFrequency, Double pageLoadTime, 
                               Double scrollDepth) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.feature = feature;
        this.action = action;
        this.engagementScore = engagementScore;
        this.responseTime = responseTime;
        this.clickCount = clickCount;
        this.errorCount = errorCount;
        this.sessionDuration = sessionDuration;
        this.timeBetweenClicks = timeBetweenClicks;
        this.errorRate = errorRate;
        this.usageFrequency = usageFrequency;
        this.pageLoadTime = pageLoadTime;
        this.scrollDepth = scrollDepth;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public Double getEngagementScore() { return engagementScore; }
    public void setEngagementScore(Double engagementScore) { this.engagementScore = engagementScore; }
    
    public Double getResponseTime() { return responseTime; }
    public void setResponseTime(Double responseTime) { this.responseTime = responseTime; }
    
    public Integer getClickCount() { return clickCount; }
    public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }
    
    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }
    
    public Double getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(Double sessionDuration) { this.sessionDuration = sessionDuration; }
    
    public Integer getTimeBetweenClicks() { return timeBetweenClicks; }
    public void setTimeBetweenClicks(Integer timeBetweenClicks) { this.timeBetweenClicks = timeBetweenClicks; }
    
    public Double getErrorRate() { return errorRate; }
    public void setErrorRate(Double errorRate) { this.errorRate = errorRate; }
    
    public Integer getUsageFrequency() { return usageFrequency; }
    public void setUsageFrequency(Integer usageFrequency) { this.usageFrequency = usageFrequency; }
    
    public Double getPageLoadTime() { return pageLoadTime; }
    public void setPageLoadTime(Double pageLoadTime) { this.pageLoadTime = pageLoadTime; }
    
    public Double getScrollDepth() { return scrollDepth; }
    public void setScrollDepth(Double scrollDepth) { this.scrollDepth = scrollDepth; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}