// src/main/java/com/boatsafari/service/DashboardStatsService.java
package com.boatsafari.service;

import com.boatsafari.model.DashboardStats;
import com.boatsafari.repository.DashboardStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardStatsService {

    @Autowired
    private DashboardStatsRepository dashboardStatsRepository;

    @Autowired
    private NewReviewService newReviewService;

    public Map<String, String> getHeroStats() {
        Map<String, String> stats = new HashMap<>();

        // Get stats from database or use defaults
        stats.put("happyCustomers", getStatValue("happy_customers", "500+"));
        stats.put("tripDestinations", getStatValue("trip_destinations", "50+"));
        stats.put("satisfactionRate", getStatValue("satisfaction_rate", "98%"));
        stats.put("ratedService", getStatValue("rated_service", "5★"));

        return stats;
    }

    private String getStatValue(String key, String defaultValue) {
        Optional<DashboardStats> stat = dashboardStatsRepository.findByKey(key);
        return stat.map(DashboardStats::getValue).orElse(defaultValue);
    }

    public void updateStat(String key, String value) {
        Optional<DashboardStats> existingStat = dashboardStatsRepository.findByKey(key);
        DashboardStats stat = existingStat.orElse(new DashboardStats(key, value));
        stat.setValue(value);
        dashboardStatsRepository.save(stat);
    }

    public long getPendingNewReviewsCount() {
        return newReviewService.getPendingReviewsCount();
    }
}