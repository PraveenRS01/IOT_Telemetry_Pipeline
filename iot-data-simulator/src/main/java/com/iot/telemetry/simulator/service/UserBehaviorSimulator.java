package com.iot.telemetry.simulator.service;

import com.iot.telemetry.simulator.models.UserBehaviorData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserBehaviorSimulator {

    private static final Logger logger = LoggerFactory.getLogger(UserBehaviorSimulator.class);
    
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
    
    public UserBehaviorData generateUserBehavior() {
        String userId = userIds.get(random.nextInt(userIds.size()));
        String sessionId = activeSessions.computeIfAbsent(userId, k -> "session_" + System.currentTimeMillis());
        String feature = features.get(random.nextInt(features.size()));
        String action = actions.get(random.nextInt(actions.size()));
        
        UserBehaviorData data = new UserBehaviorData(userId, sessionId, feature, action);
        
        // Generate realistic metrics
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("timeBetweenClicks", random.nextInt(5000) + 1000); // 1-6 seconds
        metrics.put("errorRate", random.nextDouble() * 0.1); // 0-10% error rate
        metrics.put("sessionDuration", random.nextInt(3600) + 300); // 5-60 minutes
        metrics.put("usageFrequency", random.nextInt(100) + 1); // 1-100 times
        
        data.setMetrics(metrics);
        return data;
    }

    @Scheduled(fixedRate = 5000)
    public void generateAndLogData() {
        UserBehaviorData data = generateUserBehavior();
        logger.info("Generated telemetry data: {}", data);
    }
}