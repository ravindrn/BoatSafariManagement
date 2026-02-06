// src/main/java/com/boatsafari/repository/HeroContentRepository.java
package com.boatsafari.repository;

import com.boatsafari.model.HeroContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HeroContentRepository extends JpaRepository<HeroContent, Long> {
    Optional<HeroContent> findBySectionKey(String sectionKey);
}