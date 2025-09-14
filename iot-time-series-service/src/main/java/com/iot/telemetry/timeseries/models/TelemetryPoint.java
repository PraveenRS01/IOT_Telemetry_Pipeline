package com.iot.telemetry.timeseries.models;

import java.time.Instant;
import java.util.Map;

public class TelemetryPoint {
    private String measurement;
    private Map<String, String> tags;
    private Map<String, Object> fields;
    private Instant timestamp;
    
    public TelemetryPoint() {}
    
    public TelemetryPoint(String measurement, Map<String, String> tags, Map<String, Object> fields, Instant timestamp) {
        this.measurement = measurement;
        this.tags = tags;
        this.fields = fields;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public String getMeasurement() { return measurement; }
    public void setMeasurement(String measurement) { this.measurement = measurement; }
    
    public Map<String, String> getTags() { return tags; }
    public void setTags(Map<String, String> tags) { this.tags = tags; }
    
    public Map<String, Object> getFields() { return fields; }
    public void setFields(Map<String, Object> fields) { this.fields = fields; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}