package com.iot.telemetry.timeseries.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {
    
    @Value("${influxdb.url:http://localhost:8086}")
    private String influxUrl;
    
    @Value("${influxdb.token:my-super-secret-auth-token}")
    private String influxToken;
    
    @Value("${influxdb.org:iot-telemetry}")
    private String influxOrg;
    
    @Value("${influxdb.bucket:telemetry-data}")
    private String influxBucket;
    
    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(), influxOrg, influxBucket);
    }
}