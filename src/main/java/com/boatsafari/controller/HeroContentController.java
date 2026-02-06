// src/main/java/com/boatsafari/controller/HeroContentController.java
package com.boatsafari.controller;

import com.boatsafari.model.HeroContent;
import com.boatsafari.service.HeroContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hero-content")
public class HeroContentController {

    @Autowired
    private HeroContentService heroContentService;

    @GetMapping("/{sectionKey}")
    public ResponseEntity<HeroContent> getHeroContent(@PathVariable String sectionKey) {
        HeroContent content = heroContentService.getHeroContent(sectionKey);
        return ResponseEntity.ok(content);
    }

    @PutMapping("/{sectionKey}")
    public ResponseEntity<HeroContent> updateHeroContent(
            @PathVariable String sectionKey,
            @RequestBody HeroContentUpdateRequest updateRequest) {

        HeroContent updatedContent = heroContentService.updateHeroContent(
                sectionKey,
                updateRequest.getTitle(),
                updateRequest.getDescription()
        );

        return ResponseEntity.ok(updatedContent);
    }

    // Request DTO for update
    public static class HeroContentUpdateRequest {
        private String title;
        private String description;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
