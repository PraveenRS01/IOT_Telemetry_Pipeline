package com.iot.telemetry.ingestion.repository;

import com.iot.telemetry.ingestion.models.TelemetryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryData, Long> {
    
    List<TelemetryData> findByUserId(String userId);
    
    List<TelemetryData> findBySessionId(String sessionId);
    
    List<TelemetryData> findByFeature(String feature);
    
    @Query("SELECT t FROM TelemetryData t WHERE t.timestamp BETWEEN :startTime AND :endTime")
    List<TelemetryData> findByTimestampBetween(@Param("startTime") LocalDateTime startTime, 
                                             @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(t) FROM TelemetryData t WHERE t.timestamp BETWEEN :startTime AND :endTime")
    Long countByTimestampBetween(@Param("startTime") LocalDateTime startTime, 
                                @Param("endTime") LocalDateTime endTime);
}