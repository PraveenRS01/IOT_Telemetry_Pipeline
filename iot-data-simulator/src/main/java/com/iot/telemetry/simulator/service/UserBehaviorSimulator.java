package com.iot.telemetry.simulator.service;

import com.iot.telemetry.simulator.models.UserBehaviorData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserBehaviorSimulator {

    private static final Logger logger = LoggerFactory.getLogger(UserBehaviorSimulator.class);
    
    @Autowired
    private TelemetryHttpService telemetryHttpService;
    
    private final Random random = new Random();
    private final List<String> userIds = Arrays.asList("user_001", "user_002", "user_003");
    private final Map<String, String> activeSessions = new HashMap<>();
    
    // Features that users interact with
    private final List<String> features = Arrays.asList(
        "login", "dashboard", "profile", "settings", "checkout", 
        "search", "product_details", "cart", "payment", "help"
    );
    
    // Actions users can perform
    private final List<String> actions = Arrays.asList(
        "click", "type", "submit", "navigate", "scroll", "hover", "focus", "blur"
    );
    
    // Track user behavior patterns for more realistic simulation
    private final Map<String, Integer> userClickCounts = new HashMap<>();
    private final Map<String, Double> userSessionDurations = new HashMap<>();
    private final Map<String, Integer> userErrorCounts = new HashMap<>();
    
    public UserBehaviorData generateUserBehavior() {
        String userId = userIds.get(random.nextInt(userIds.size()));
        String sessionId = activeSessions.computeIfAbsent(userId, k -> "session_" + System.currentTimeMillis());
        String feature = features.get(random.nextInt(features.size()));
        String action = actions.get(random.nextInt(actions.size()));
        
        UserBehaviorData data = new UserBehaviorData(userId, sessionId, feature, action);
        
        // Generate realistic metrics that align with stream processing
        Map<String, Object> metrics = generateRealisticMetrics(userId, feature, action);
        data.setMetrics(metrics);
        
        return data;
    }
    
    private Map<String, Object> generateRealisticMetrics(String userId, String feature, String action) {
        Map<String, Object> metrics = new HashMap<>();
        
        // 1. Response Time (milliseconds) - varies by feature complexity
        double responseTime = generateResponseTime(feature, action);
        metrics.put("responseTime", responseTime);
        
        // 2. Error Count - cumulative errors in current session
        int errorCount = generateErrorCount(userId, feature);
        metrics.put("errorCount", errorCount);
        
        // 3. Click Count - cumulative clicks in current session
        int clickCount = generateClickCount(userId, feature, action);
        metrics.put("clickCount", clickCount);
        
        // 4. Session Duration (seconds) - how long user has been active
        double sessionDuration = generateSessionDuration(userId);
        metrics.put("sessionDuration", sessionDuration);
        
        // 5. Time Between Clicks (milliseconds) - interaction frequency
        int timeBetweenClicks = generateTimeBetweenClicks(feature, action);
        metrics.put("timeBetweenClicks", timeBetweenClicks);
        
        // 6. Error Rate (percentage) - calculated from error count and total actions
        double errorRate = calculateErrorRate(errorCount, clickCount);
        metrics.put("errorRate", errorRate);
        
        // 7. Usage Frequency - how often this feature is used
        int usageFrequency = generateUsageFrequency(feature);
        metrics.put("usageFrequency", usageFrequency);
        
        // 8. Page Load Time (milliseconds) - for web features
        double pageLoadTime = generatePageLoadTime(feature);
        metrics.put("pageLoadTime", pageLoadTime);
        
        // 9. Scroll Depth (percentage) - how much of page user scrolled
        double scrollDepth = generateScrollDepth(feature, action);
        metrics.put("scrollDepth", scrollDepth);
        
        return metrics;
    }
    
    private double generateResponseTime(String feature, String action) {
        // Different features have different response time characteristics
        double baseResponseTime;
        
        switch (feature) {
            case "login":
                baseResponseTime = 800 + random.nextGaussian() * 200; // 600-1000ms
                break;
            case "dashboard":
                baseResponseTime = 1200 + random.nextGaussian() * 300; // 900-1500ms
                break;
            case "search":
                baseResponseTime = 2000 + random.nextGaussian() * 500; // 1500-2500ms
                break;
            case "payment":
                baseResponseTime = 3000 + random.nextGaussian() * 1000; // 2000-4000ms
                break;
            case "product_details":
                baseResponseTime = 1500 + random.nextGaussian() * 400; // 1100-1900ms
                break;
            default:
                baseResponseTime = 1000 + random.nextGaussian() * 300; // 700-1300ms
        }
        
        // Action type affects response time
        if (action.equals("submit")) {
            baseResponseTime *= 1.5; // Submit actions take longer
        } else if (action.equals("hover")) {
            baseResponseTime *= 0.3; // Hover actions are instant
        }
        
        // Occasionally generate high response times (anomalies)
        if (random.nextDouble() < 0.05) { // 5% chance
            baseResponseTime += random.nextDouble() * 5000; // Add up to 5 seconds
        }
        
        return Math.max(50, baseResponseTime); // Minimum 50ms
    }
    
    private int generateErrorCount(String userId, String feature) {
        // Track cumulative errors per user
        String key = userId + "_" + feature;
        int currentErrors = userErrorCounts.getOrDefault(key, 0);
        
        // Probability of error based on feature complexity
        double errorProbability;
        switch (feature) {
            case "payment":
                errorProbability = 0.15; // 15% chance of error
                break;
            case "login":
                errorProbability = 0.08; // 8% chance of error
                break;
            case "search":
                errorProbability = 0.05; // 5% chance of error
                break;
            default:
                errorProbability = 0.03; // 3% chance of error
        }
        
        if (random.nextDouble() < errorProbability) {
            currentErrors += random.nextInt(3) + 1; // 1-3 errors
        }
        
        userErrorCounts.put(key, currentErrors);
        return currentErrors;
    }
    
    private int generateClickCount(String userId, String feature, String action) {
        // Track cumulative clicks per user
        String key = userId + "_" + feature;
        int currentClicks = userClickCounts.getOrDefault(key, 0);
        
        // Increment click count based on action
        int clickIncrement = 1;
        if (action.equals("click")) {
            clickIncrement = random.nextInt(3) + 1; // 1-3 clicks
        } else if (action.equals("type")) {
            clickIncrement = random.nextInt(5) + 1; // 1-5 clicks (typing)
        }
        
        currentClicks += clickIncrement;
        userClickCounts.put(key, currentClicks);
        
        // Occasionally generate high click counts (anomalies)
        if (random.nextDouble() < 0.02) { // 2% chance
            currentClicks += random.nextInt(20) + 10; // Add 10-30 clicks
        }
        
        return currentClicks;
    }
    
    private double generateSessionDuration(String userId) {
        // Track session duration per user
        double currentDuration = userSessionDurations.getOrDefault(userId, 0.0);
        
        // Add time increment (5-30 seconds per interaction)
        double timeIncrement = 5 + random.nextDouble() * 25;
        currentDuration += timeIncrement;
        
        // Reset session after 2 hours
        if (currentDuration > 7200) { // 2 hours in seconds
            currentDuration = timeIncrement;
        }
        
        userSessionDurations.put(userId, currentDuration);
        return currentDuration;
    }
    
    private int generateTimeBetweenClicks(String feature, String action) {
        // Time between clicks varies by feature and action
        int baseTime;
        
        switch (feature) {
            case "search":
                baseTime = 2000 + random.nextInt(3000); // 2-5 seconds
                break;
            case "payment":
                baseTime = 3000 + random.nextInt(5000); // 3-8 seconds
                break;
            case "login":
                baseTime = 1000 + random.nextInt(2000); // 1-3 seconds
                break;
            default:
                baseTime = 1500 + random.nextInt(2500); // 1.5-4 seconds
        }
        
        // Action type affects timing
        if (action.equals("type")) {
            baseTime += random.nextInt(2000); // Typing takes longer
        } else if (action.equals("hover")) {
            baseTime = random.nextInt(500); // Hover is quick
        }
        
        return baseTime;
    }
    
    private double calculateErrorRate(int errorCount, int clickCount) {
        if (clickCount == 0) return 0.0;
        return (double) errorCount / clickCount * 100.0; // Percentage
    }
    
    private int generateUsageFrequency(String feature) {
        // Some features are used more frequently
        switch (feature) {
            case "dashboard":
                return random.nextInt(50) + 20; // 20-70 times
            case "search":
                return random.nextInt(30) + 10; // 10-40 times
            case "login":
                return random.nextInt(10) + 1; // 1-11 times
            case "payment":
                return random.nextInt(5) + 1; // 1-6 times
            default:
                return random.nextInt(20) + 5; // 5-25 times
        }
    }
    
    private double generatePageLoadTime(String feature) {
        // Page load time varies by feature complexity
        double baseLoadTime;
        
        switch (feature) {
            case "dashboard":
                baseLoadTime = 1500 + random.nextGaussian() * 300; // 1200-1800ms
                break;
            case "product_details":
                baseLoadTime = 2000 + random.nextGaussian() * 500; // 1500-2500ms
                break;
            case "search":
                baseLoadTime = 1000 + random.nextGaussian() * 200; // 800-1200ms
                break;
            default:
                baseLoadTime = 1200 + random.nextGaussian() * 400; // 800-1600ms
        }
        
        return Math.max(200, baseLoadTime); // Minimum 200ms
    }
    
    private double generateScrollDepth(String feature, String action) {
        // Scroll depth varies by feature and action
        if (action.equals("scroll")) {
            return 20 + random.nextDouble() * 80; // 20-100%
        } else if (feature.equals("product_details")) {
            return 40 + random.nextDouble() * 60; // 40-100%
        } else {
            return random.nextDouble() * 100; // 0-100%
        }
    }

    @Scheduled(fixedRate = 5000)
    public void generateAndLogData() {
        UserBehaviorData data = generateUserBehavior();
        logger.info("Generated telemetry data: {}", data);

        // Send data to ingestion service
        telemetryHttpService.sendTelemetryData(data);
    }
}