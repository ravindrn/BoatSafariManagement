// src/main/java/com/boatsafari/service/HeroContentService.java
package com.boatsafari.service;

import com.boatsafari.model.HeroContent;
import com.boatsafari.repository.HeroContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HeroContentService {

    @Autowired
    private HeroContentRepository heroContentRepository;

    public HeroContent getHeroContent(String sectionKey) {
        Optional<HeroContent> content = heroContentRepository.findBySectionKey(sectionKey);
        return content.orElseGet(() -> createDefaultContent(sectionKey));
    }

    public HeroContent updateHeroContent(String sectionKey, String title, String description) {
        Optional<HeroContent> existingContent = heroContentRepository.findBySectionKey(sectionKey);
        HeroContent content;

        if (existingContent.isPresent()) {
            content = existingContent.get();
            content.setTitle(title);
            content.setDescription(description);
        } else {
            content = new HeroContent(sectionKey, title, description);
        }

        return heroContentRepository.save(content);
    }

    private HeroContent createDefaultContent(String sectionKey) {
        if ("main_hero".equals(sectionKey)) {
            return new HeroContent(
                    sectionKey,
                    "Experience the Ultimate Boat Safari Adventure",
                    "Discover breathtaking rivers, exotic wildlife, and unforgettable experiences with our premium boat safari trips. Book your adventure today!"
            );
        }
        return new HeroContent(sectionKey, "Default Title", "Default Description");
    }
}