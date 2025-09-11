-- Connect to analytics database
\c telemetry_analytics;

-- Create user engagement summary table (updated with all realistic metrics)
CREATE TABLE user_engagement_summary (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    feature VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    
    -- Core engagement metrics
    engagement_score DECIMAL(5,2) NOT NULL,
    response_time DECIMAL(10,2) NOT NULL,
    click_count INTEGER NOT NULL,
    error_count INTEGER NOT NULL,
    session_duration DECIMAL(10,2) NOT NULL,
    
    -- Additional realistic metrics
    time_between_clicks INTEGER,
    error_rate DECIMAL(5,2),
    usage_frequency INTEGER,
    page_load_time DECIMAL(10,2),
    scroll_depth DECIMAL(5,2),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create feature analytics table
CREATE TABLE feature_analytics (
    id SERIAL PRIMARY KEY,
    feature VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    total_sessions INTEGER NOT NULL,
    average_engagement_score DECIMAL(5,2) NOT NULL,
    average_response_time DECIMAL(10,2) NOT NULL,
    total_errors INTEGER NOT NULL,
    total_clicks INTEGER NOT NULL,
    average_page_load_time DECIMAL(10,2),
    average_scroll_depth DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create anomaly events table
CREATE TABLE anomaly_events (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    feature VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    anomaly_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    threshold_value DECIMAL(10,2) NOT NULL,
    actual_value DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_user_engagement_user_id ON user_engagement_summary(user_id);
CREATE INDEX idx_user_engagement_feature ON user_engagement_summary(feature);
CREATE INDEX idx_user_engagement_created_at ON user_engagement_summary(created_at);
CREATE INDEX idx_feature_analytics_feature_date ON feature_analytics(feature, date);
CREATE INDEX idx_anomaly_events_user_id ON anomaly_events(user_id);
CREATE INDEX idx_anomaly_events_created_at ON anomaly_events(created_at);

-- Insert sample data with realistic metrics
INSERT INTO user_engagement_summary (user_id, session_id, feature, action, engagement_score, response_time, click_count, error_count, session_duration, time_between_clicks, error_rate, usage_frequency, page_load_time, scroll_depth) VALUES
('user_001', 'session_001', 'login', 'click', 85.5, 750.0, 5, 0, 300.0, 1500, 0.0, 10, 800.0, 45.5),
('user_001', 'session_001', 'dashboard', 'hover', 78.2, 1200.0, 12, 1, 600.0, 2000, 8.33, 25, 1500.0, 60.2),
('user_002', 'session_002', 'search', 'type', 92.1, 500.0, 8, 0, 450.0, 1000, 0.0, 15, 1000.0, 75.8);