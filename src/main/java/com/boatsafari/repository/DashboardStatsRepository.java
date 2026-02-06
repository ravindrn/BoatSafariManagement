// src/main/java/com/boatsafari/repository/DashboardStatsRepository.java
package com.boatsafari.repository;

import com.boatsafari.model.DashboardStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DashboardStatsRepository extends JpaRepository<DashboardStats, Long> {
    Optional<DashboardStats> findByKey(String key);
}