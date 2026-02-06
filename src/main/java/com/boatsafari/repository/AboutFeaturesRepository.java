// src/main/java/com/boatsafari/repository/AboutFeaturesRepository.java
package com.boatsafari.repository;

import com.boatsafari.model.AboutFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AboutFeaturesRepository extends JpaRepository<AboutFeatures, Long> {
    List<AboutFeatures> findByActiveTrueOrderByDisplayOrderAsc();
    List<AboutFeatures> findAllByOrderByDisplayOrderAsc();
}
