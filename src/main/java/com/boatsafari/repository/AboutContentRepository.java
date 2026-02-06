// src/main/java/com/boatsafari/repository/AboutContentRepository.java
package com.boatsafari.repository;

import com.boatsafari.model.AboutContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AboutContentRepository extends JpaRepository<AboutContent, Long> {
    Optional<AboutContent> findBySectionKey(String sectionKey);
}