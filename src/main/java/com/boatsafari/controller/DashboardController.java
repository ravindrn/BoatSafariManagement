// src/main/java/com/boatsafari/controller/DashboardController.java
package com.boatsafari.controller;

import com.boatsafari.model.HeroContent;
import com.boatsafari.service.DashboardStatsService;
import com.boatsafari.service.HeroContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardStatsService dashboardStatsService;

    @Autowired
    private HeroContentService heroContentService;

    // Get hero statistics only
    @GetMapping("/hero-stats")
    public Map<String, String> getHeroStats() {
        return dashboardStatsService.getHeroStats();
    }

    // Update individual hero statistic
    @PutMapping("/hero-stats/{key}")
    public ResponseEntity<String> updateHeroStat(@PathVariable String key, @RequestBody String value) {
        try {
            dashboardStatsService.updateStat(key, value);
            return ResponseEntity.ok("Statistic updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating statistic: " + e.getMessage());
        }
    }

    // Get complete hero data (title, description, and stats)
    @GetMapping("/hero-data")
    public Map<String, Object> getHeroData() {
        Map<String, Object> heroData = new HashMap<>();

        try {
            // Get hero content (title and description)
            HeroContent heroContent = heroContentService.getHeroContent("main_hero");
            heroData.put("title", heroContent.getTitle());
            heroData.put("description", heroContent.getDescription());

            // Get hero statistics
            Map<String, String> stats = dashboardStatsService.getHeroStats();
            heroData.putAll(stats);

        } catch (Exception e) {
            // Fallback to default values if there's an error
            heroData.put("title", "Experience the Ultimate Boat Safari Adventure");
            heroData.put("description", "Discover breathtaking rivers, exotic wildlife, and unforgettable experiences with our premium boat safari trips. Book your adventure today!");
            heroData.put("happyCustomers", "500+");
            heroData.put("tripDestinations", "50+");
            heroData.put("satisfactionRate", "98%");
            heroData.put("ratedService", "5★");
        }

        return heroData;
    }

    // Get only hero content (title and description)
    @GetMapping("/hero-content")
    public ResponseEntity<Map<String, String>> getHeroContent() {
        try {
            HeroContent heroContent = heroContentService.getHeroContent("main_hero");
            Map<String, String> content = new HashMap<>();
            content.put("title", heroContent.getTitle());
            content.put("description", heroContent.getDescription());
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update hero content (title, description, AND stats) - UPDATED VERSION
    @PutMapping("/hero-content")
    public ResponseEntity<Map<String, Object>> updateHeroContent(@RequestBody HeroContentUpdateRequest updateRequest) {
        try {
            System.out.println("Updating hero content: " + updateRequest.getTitle() + " - " + updateRequest.getDescription());
            System.out.println("Updating stats - Happy Customers: " + updateRequest.getHappyCustomers());
            System.out.println("Updating stats - Trip Destinations: " + updateRequest.getTripDestinations());
            System.out.println("Updating stats - Satisfaction Rate: " + updateRequest.getSatisfactionRate());
            System.out.println("Updating stats - Rated Service: " + updateRequest.getRatedService());

            // 1. Update hero content (title and description)
            HeroContent updatedContent = heroContentService.updateHeroContent(
                    "main_hero",
                    updateRequest.getTitle(),
                    updateRequest.getDescription()
            );

            // 2. Update statistics - ADDED THIS SECTION
            if (updateRequest.getHappyCustomers() != null) {
                dashboardStatsService.updateStat("happy_customers", updateRequest.getHappyCustomers());
            }
            if (updateRequest.getTripDestinations() != null) {
                dashboardStatsService.updateStat("trip_destinations", updateRequest.getTripDestinations());
            }
            if (updateRequest.getSatisfactionRate() != null) {
                dashboardStatsService.updateStat("satisfaction_rate", updateRequest.getSatisfactionRate());
            }
            if (updateRequest.getRatedService() != null) {
                dashboardStatsService.updateStat("rated_service", updateRequest.getRatedService());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hero content and stats updated successfully");
            response.put("data", updatedContent);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error updating hero content: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error updating hero content: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Updated Request DTO for hero content updates - INCLUDES STATS FIELDS
    public static class HeroContentUpdateRequest {
        private String title;
        private String description;
        private String happyCustomers;
        private String tripDestinations;
        private String satisfactionRate;
        private String ratedService;

        // Getters and Setters for all fields
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getHappyCustomers() {
            return happyCustomers;
        }

        public void setHappyCustomers(String happyCustomers) {
            this.happyCustomers = happyCustomers;
        }

        public String getTripDestinations() {
            return tripDestinations;
        }

        public void setTripDestinations(String tripDestinations) {
            this.tripDestinations = tripDestinations;
        }

        public String getSatisfactionRate() {
            return satisfactionRate;
        }

        public void setSatisfactionRate(String satisfactionRate) {
            this.satisfactionRate = satisfactionRate;
        }

        public String getRatedService() {
            return ratedService;
        }

        public void setRatedService(String ratedService) {
            this.ratedService = ratedService;
        }

        @Override
        public String toString() {
            return "HeroContentUpdateRequest{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", happyCustomers='" + happyCustomers + '\'' +
                    ", tripDestinations='" + tripDestinations + '\'' +
                    ", satisfactionRate='" + satisfactionRate + '\'' +
                    ", ratedService='" + ratedService + '\'' +
                    '}';
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Dashboard Controller");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Get all stats (alternative endpoint)
    @GetMapping("/stats")
    public ResponseEntity<Map<String, String>> getAllStats() {
        try {
            Map<String, String> stats = dashboardStatsService.getHeroStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Update multiple stats at once
    @PutMapping("/stats/bulk")
    public ResponseEntity<Map<String, Object>> updateStatsBulk(@RequestBody Map<String, String> statsUpdates) {
        try {
            System.out.println("Bulk updating stats: " + statsUpdates);

            for (Map.Entry<String, String> entry : statsUpdates.entrySet()) {
                dashboardStatsService.updateStat(entry.getKey(), entry.getValue());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stats updated successfully");
            response.put("updatedStats", statsUpdates);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error bulk updating stats: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error updating stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}