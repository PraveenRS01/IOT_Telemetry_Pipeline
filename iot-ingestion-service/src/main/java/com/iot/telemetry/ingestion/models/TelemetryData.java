package com.iot.telemetry.ingestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_data")
public class TelemetryData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @NotBlank(message = "Session ID is required")
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @NotBlank(message = "Feature is required")
    @Column(name = "feature", nullable = false)
    private String feature;
    
    @NotBlank(message = "Action is required")
    @Column(name = "action", nullable = false)
    private String action;
    
    @NotNull(message = "Timestamp is required")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "metrics", columnDefinition = "TEXT")
    private String metricsJson; // Store as JSON string for now
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public TelemetryData() {
        this.createdAt = LocalDateTime.now();
    }
    
    public TelemetryData(String userId, String sessionId, String feature, String action, LocalDateTime timestamp) {
        this();
        this.userId = userId;
        this.sessionId = sessionId;
        this.feature = feature;
        this.action = action;
        this.timestamp = timestamp;
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
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getMetricsJson() { return metricsJson; }
    public void setMetricsJson(String metricsJson) { this.metricsJson = metricsJson; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "TelemetryData{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", feature='" + feature + '\'' +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                ", createdAt=" + createdAt +
                '}';
    }
}