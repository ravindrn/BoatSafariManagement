// src/main/java/com/boatsafari/service/AboutStatsService.java
package com.boatsafari.service;

import com.boatsafari.model.AboutStats;
import com.boatsafari.repository.AboutStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AboutStatsService {

    @Autowired
    private AboutStatsRepository aboutStatsRepository;

    public Map<String, AboutStats> getAboutStats() {
        List<AboutStats> statsList = aboutStatsRepository.findAllByOrderByIdAsc();
        Map<String, AboutStats> statsMap = new HashMap<>();

        for (AboutStats stat : statsList) {
            statsMap.put(stat.getKey(), stat);
        }

        return statsMap;
    }

    public AboutStats updateStat(String key, String value, String label) {
        Optional<AboutStats> existingStat = aboutStatsRepository.findByKey(key);
        AboutStats stat;

        if (existingStat.isPresent()) {
            stat = existingStat.get();
            stat.setValue(value);
            if (label != null) {
                stat.setLabel(label);
            }
        } else {
            stat = new AboutStats(key, value, label != null ? label : key);
        }

        return aboutStatsRepository.save(stat);
    }

    public AboutStats getStatByKey(String key) {
        return aboutStatsRepository.findByKey(key).orElse(null);
    }
}