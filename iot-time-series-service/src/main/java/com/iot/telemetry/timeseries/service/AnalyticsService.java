package com.iot.telemetry.timeseries.service;

import com.iot.telemetry.timeseries.models.UserEngagementSummary;
import com.iot.telemetry.timeseries.repository.UserEngagementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    
    @Autowired
    private UserEngagementRepository userEngagementRepository;

    public void storeEngagementSummary(String userId, String sessionId, String feature, String action, 
                                     double engagementScore, Map<String, Object> metrics) {
        try {
            UserEngagementSummary summary = new UserEngagementSummary();
            summary.setUserId(userId);
            summary.setSessionId(sessionId);
            summary.setFeature(feature);
            summary.setAction(action);
            summary.setEngagementScore(engagementScore);
            
            // Extract metrics
            summary.setResponseTime(getDoubleValue(metrics, "responseTime", 0.0));
            summary.setClickCount(getIntValue(metrics, "clickCount", 0));
            summary.setErrorCount(getIntValue(metrics, "errorCount", 0));
            summary.setSessionDuration(getDoubleValue(metrics, "sessionDuration", 0.0));
            summary.setTimeBetweenClicks(getIntValue(metrics, "timeBetweenClicks", 0));
            summary.setErrorRate(getDoubleValue(metrics, "errorRate", 0.0));
            summary.setUsageFrequency(getIntValue(metrics, "usageFrequency", 0));
            summary.setPageLoadTime(getDoubleValue(metrics, "pageLoadTime", 0.0));
            summary.setScrollDepth(getDoubleValue(metrics, "scrollDepth", 0.0));
            
            summary.setCreatedAt(LocalDateTime.now());
            summary.setUpdatedAt(LocalDateTime.now());
            
            userEngagementRepository.save(summary);
            logger.info("Successfully stored engagement summary for user: {}, feature: {}", userId, feature);
            
        } catch (Exception e) {
            logger.error("Error storing engagement summary", e);
        }
    }
    
    public List<UserEngagementSummary> getUserEngagementData(String userId) {
        return userEngagementRepository.findByUserId(userId);
    }
    
    public List<UserEngagementSummary> getFeatureEngagementData(String feature) {
        return userEngagementRepository.findByFeature(feature);
    }
    
    public List<UserEngagementSummary> getRecentEngagementData(int limit) {
        return userEngagementRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    public long getTotalRecords() {
        return userEngagementRepository.count();
    }
    
    private double getDoubleValue(Map<String, Object> metrics, String key, double defaultValue) {
        Object value = metrics.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
    
    private int getIntValue(Map<String, Object> metrics, String key, int defaultValue) {
        Object value = metrics.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
