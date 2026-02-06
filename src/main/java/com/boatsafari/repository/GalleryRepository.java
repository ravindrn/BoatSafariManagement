// src/main/java/com/boatsafari/repository/GalleryRepository.java
package com.boatsafari.repository;

import com.boatsafari.model.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    List<Gallery> findByActiveTrue();
}