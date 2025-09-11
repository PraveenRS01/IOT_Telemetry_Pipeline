package com.iot.telemetry.timeseries.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class InfluxDBService {
    
    private static final Logger logger = LoggerFactory.getLogger(InfluxDBService.class);
    
    @Autowired
    private InfluxDBClient influxDBClient;
    
    public void writeTelemetryData(String userId, String sessionId, String feature, String action, 
                                 Map<String, Object> metrics) {
        try {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
            
            // Create InfluxDB point for raw telemetry data
            Point point = Point.measurement("telemetry_data")
                .addTag("user_id", userId)
                .addTag("session_id", sessionId)
                .addTag("feature", feature)
                .addTag("action", action)
                .time(Instant.now(), WritePrecision.MS);
            
            // Add all realistic metrics as fields
            addMetricField(point, "responseTime", metrics.get("responseTime"));
            addMetricField(point, "errorCount", metrics.get("errorCount"));
            addMetricField(point, "clickCount", metrics.get("clickCount"));
            addMetricField(point, "sessionDuration", metrics.get("sessionDuration"));
            addMetricField(point, "timeBetweenClicks", metrics.get("timeBetweenClicks"));
            addMetricField(point, "errorRate", metrics.get("errorRate"));
            addMetricField(point, "usageFrequency", metrics.get("usageFrequency"));
            addMetricField(point, "pageLoadTime", metrics.get("pageLoadTime"));
            addMetricField(point, "scrollDepth", metrics.get("scrollDepth"));
            
            // Write to InfluxDB
            writeApi.writePoint(point);
            logger.info("Successfully wrote telemetry data to InfluxDB for user: {}, feature: {}", userId, feature);
            
        } catch (Exception e) {
            logger.error("Error writing telemetry data to InfluxDB", e);
        }
    }
    
    public void writeEngagementMetrics(String userId, String sessionId, String feature, String action,
                                     double engagementScore, Map<String, Object> metrics) {
        try {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
            
            Point point = Point.measurement("engagement_metrics")
                .addTag("user_id", userId)
                .addTag("session_id", sessionId)
                .addTag("feature", feature)
                .addTag("action", action)
                .addField("engagement_score", engagementScore)
                .time(Instant.now(), WritePrecision.MS);
            
            // Add all metrics as fields
            addMetricField(point, "responseTime", metrics.get("responseTime"));
            addMetricField(point, "errorCount", metrics.get("errorCount"));
            addMetricField(point, "clickCount", metrics.get("clickCount"));
            addMetricField(point, "sessionDuration", metrics.get("sessionDuration"));
            addMetricField(point, "timeBetweenClicks", metrics.get("timeBetweenClicks"));
            addMetricField(point, "errorRate", metrics.get("errorRate"));
            addMetricField(point, "usageFrequency", metrics.get("usageFrequency"));
            addMetricField(point, "pageLoadTime", metrics.get("pageLoadTime"));
            addMetricField(point, "scrollDepth", metrics.get("scrollDepth"));
            
            writeApi.writePoint(point);
            logger.info("Successfully wrote engagement metrics to InfluxDB for user: {}, feature: {}", userId, feature);
            
        } catch (Exception e) {
            logger.error("Error writing engagement metrics to InfluxDB", e);
        }
    }
    
    private void addMetricField(Point point, String fieldName, Object value) {
        if (value != null) {
            if (value instanceof Number) {
                point.addField(fieldName, (Number) value);
            } else if (value instanceof String) {
                point.addField(fieldName, (String) value);
            } else if (value instanceof Boolean) {
                point.addField(fieldName, (Boolean) value);
            }
        }
    }
}