// src/main/java/com/boatsafari/service/AboutContentService.java
package com.boatsafari.service;

import com.boatsafari.model.AboutContent;
import com.boatsafari.repository.AboutContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AboutContentService {

    @Autowired
    private AboutContentRepository aboutContentRepository;

    public AboutContent getAboutContent(String sectionKey) {
        Optional<AboutContent> content = aboutContentRepository.findBySectionKey(sectionKey);
        return content.orElseGet(() -> createDefaultContent(sectionKey));
    }

    public AboutContent updateAboutContent(String sectionKey, AboutContent aboutContent) {
        Optional<AboutContent> existingContent = aboutContentRepository.findBySectionKey(sectionKey);
        AboutContent content;

        if (existingContent.isPresent()) {
            content = existingContent.get();
            content.setTitle(aboutContent.getTitle());
            content.setSubtitle(aboutContent.getSubtitle());
            content.setDescription1(aboutContent.getDescription1());
            content.setDescription2(aboutContent.getDescription2());
        } else {
            content = aboutContent;
            content.setSectionKey(sectionKey);
        }

        return aboutContentRepository.save(content);
    }

    private AboutContent createDefaultContent(String sectionKey) {
        if ("main_about".equals(sectionKey)) {
            return new AboutContent(
                    sectionKey,
                    "About Boat Safari",
                    "Your Premier Boat Safari Experience",
                    "Founded in 2010, Boat Safari has been providing unforgettable river exploration experiences to adventure seekers and nature lovers from around the world. Our mission is to connect people with nature through safe, educational, and thrilling boat safari adventures.",
                    "We operate a fleet of modern, well-maintained boats and employ experienced guides who are passionate about wildlife and conservation. Our trips are designed to showcase the beauty of river ecosystems while promoting sustainable tourism practices."
            );
        }
        return new AboutContent(sectionKey, "About Us", "Our Story", "Default description 1", "Default description 2");
    }
}