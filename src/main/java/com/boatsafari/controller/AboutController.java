// src/main/java/com/boatsafari/controller/AboutController.java
package com.boatsafari.controller;

import com.boatsafari.model.AboutContent;
import com.boatsafari.model.AboutFeatures;
import com.boatsafari.model.AboutStats;
import com.boatsafari.service.AboutContentService;
import com.boatsafari.service.AboutFeaturesService;
import com.boatsafari.service.AboutStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/about")
@CrossOrigin(origins = "*")
public class AboutController {

    @Autowired
    private AboutStatsService aboutStatsService;

    @Autowired
    private AboutFeaturesService aboutFeaturesService;

    @Autowired
    private AboutContentService aboutContentService;

    // Get complete about section data
    @GetMapping("/section-data")
    public ResponseEntity<?> getAboutSectionData() {
        try {
            Map<String, Object> response = new HashMap<>();

            // Get stats
            Map<String, AboutStats> stats = aboutStatsService.getAboutStats();
            Map<String, Object> statsMap = new HashMap<>();

            for (Map.Entry<String, AboutStats> entry : stats.entrySet()) {
                Map<String, Object> statData = new HashMap<>();
                statData.put("value", entry.getValue().getValue());
                statData.put("label", entry.getValue().getLabel());
                statsMap.put(entry.getKey(), statData);
            }
            response.put("stats", statsMap);

            // Get content
            AboutContent content = aboutContentService.getAboutContent("main_about");
            response.put("title", content.getTitle());
            response.put("subtitle", content.getSubtitle());
            response.put("description1", content.getDescription1());
            response.put("description2", content.getDescription2());

            // Get features
            List<AboutFeatures> features = aboutFeaturesService.getActiveFeatures();
            response.put("features", features);

            Map<String, Object> finalResponse = new HashMap<>();
            finalResponse.put("success", true);
            finalResponse.put("data", response);

            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error loading about section data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Update about section data
    @PutMapping("/section-data")
    public ResponseEntity<?> updateAboutSectionData(@RequestBody Map<String, Object> aboutData) {
        try {
            // Update content
            AboutContent content = new AboutContent();
            content.setTitle((String) aboutData.get("title"));
            content.setSubtitle((String) aboutData.get("subtitle"));
            content.setDescription1((String) aboutData.get("description1"));
            content.setDescription2((String) aboutData.get("description2"));
            aboutContentService.updateAboutContent("main_about", content);

            // Update stats
            @SuppressWarnings("unchecked")
            Map<String, Object> stats = (Map<String, Object>) aboutData.get("stats");

            if (stats != null) {
                updateStat(stats, "years_experience", "Years Experience");
                updateStat(stats, "expert_guides", "Expert Guides");
                updateStat(stats, "modern_boats", "Modern Boats");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "About section updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating about section: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private void updateStat(Map<String, Object> stats, String statKey, String defaultLabel) {
        @SuppressWarnings("unchecked")
        Map<String, Object> statData = (Map<String, Object>) stats.get(statKey);

        if (statData != null) {
            String value = statData.get("value").toString();
            String label = statData.containsKey("label") ? statData.get("label").toString() : defaultLabel;
            aboutStatsService.updateStat(statKey, value, label);
        }
    }

    // Stats endpoints
    @GetMapping("/stats")
    public ResponseEntity<?> getAllStats() {
        try {
            Map<String, AboutStats> stats = aboutStatsService.getAboutStats();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error loading stats: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/stats/{key}")
    public ResponseEntity<?> updateStat(@PathVariable String key, @RequestBody Map<String, String> statData) {
        try {
            AboutStats stat = aboutStatsService.updateStat(key, statData.get("value"), statData.get("label"));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stat);
            response.put("message", "Stat updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating stat: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Features endpoints
    @GetMapping("/features")
    public ResponseEntity<?> getAllFeatures() {
        try {
            List<AboutFeatures> features = aboutFeaturesService.getAllFeatures();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", features);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error loading features: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/features/active")
    public ResponseEntity<?> getActiveFeatures() {
        try {
            List<AboutFeatures> features = aboutFeaturesService.getActiveFeatures();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", features);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error loading active features: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}