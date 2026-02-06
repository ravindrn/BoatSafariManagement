// src/main/java/com/boatsafari/service/AboutFeaturesService.java
package com.boatsafari.service;

import com.boatsafari.model.AboutFeatures;
import com.boatsafari.repository.AboutFeaturesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AboutFeaturesService {

    @Autowired
    private AboutFeaturesRepository aboutFeaturesRepository;

    public List<AboutFeatures> getActiveFeatures() {
        return aboutFeaturesRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public List<AboutFeatures> getAllFeatures() {
        return aboutFeaturesRepository.findAllByOrderByDisplayOrderAsc();
    }

    public AboutFeatures getFeatureById(Long id) {
        return aboutFeaturesRepository.findById(id).orElse(null);
    }

    public AboutFeatures createFeature(AboutFeatures feature) {
        return aboutFeaturesRepository.save(feature);
    }

    public AboutFeatures updateFeature(Long id, AboutFeatures featureDetails) {
        AboutFeatures feature = aboutFeaturesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature not found with id: " + id));

        feature.setIcon(featureDetails.getIcon());
        feature.setTitle(featureDetails.getTitle());
        feature.setDescription(featureDetails.getDescription());
        feature.setColor(featureDetails.getColor());
        feature.setDisplayOrder(featureDetails.getDisplayOrder());
        feature.setActive(featureDetails.getActive());

        return aboutFeaturesRepository.save(feature);
    }

    public void deleteFeature(Long id) {
        aboutFeaturesRepository.deleteById(id);
    }

    public AboutFeatures updateFeatureStatus(Long id, Boolean active) {
        AboutFeatures feature = aboutFeaturesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature not found with id: " + id));

        feature.setActive(active);
        return aboutFeaturesRepository.save(feature);
    }
}