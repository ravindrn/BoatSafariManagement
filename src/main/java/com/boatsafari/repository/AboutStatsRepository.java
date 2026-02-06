// src/main/java/com/boatsafari/repository/AboutStatsRepository.java
package com.boatsafari.repository;

import com.boatsafari.model.AboutStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AboutStatsRepository extends JpaRepository<AboutStats, Long> {
    Optional<AboutStats> findByKey(String key);

    @Query("SELECT s FROM AboutStats s ORDER BY s.id ASC")
    List<AboutStats> findAllByOrderByIdAsc();
}