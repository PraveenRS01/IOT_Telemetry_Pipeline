package com.iot.telemetry.timeseries.repository;

import com.iot.telemetry.timeseries.models.UserEngagementSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserEngagementRepository extends JpaRepository<UserEngagementSummary, Long> {
    
    List<UserEngagementSummary> findByUserId(String userId);
    
    List<UserEngagementSummary> findByFeature(String feature);
    
    List<UserEngagementSummary> findByUserIdAndFeature(String userId, String feature);
    
    @Query("SELECT u FROM UserEngagementSummary u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    List<UserEngagementSummary> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(u.engagementScore) FROM UserEngagementSummary u WHERE u.feature = :feature")
    Double getAverageEngagementScoreByFeature(@Param("feature") String feature);
    
    @Query("SELECT AVG(u.responseTime) FROM UserEngagementSummary u WHERE u.feature = :feature")
    Double getAverageResponseTimeByFeature(@Param("feature") String feature);
    
    @Query("SELECT COUNT(u) FROM UserEngagementSummary u WHERE u.feature = :feature")
    Long getTotalSessionsByFeature(@Param("feature") String feature);
    
    List<UserEngagementSummary> findTop10ByOrderByCreatedAtDesc();
}