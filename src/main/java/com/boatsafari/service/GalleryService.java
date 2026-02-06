// src/main/java/com/boatsafari/service/GalleryService.java
package com.boatsafari.service;

import com.boatsafari.model.Gallery;
import com.boatsafari.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    public List<Gallery> getAllGalleryItems() {
        return galleryRepository.findAll();
    }

    public List<Gallery> getActiveGalleryItems() {
        return galleryRepository.findByActiveTrue();
    }

    public Optional<Gallery> getGalleryItemById(Long id) {
        return galleryRepository.findById(id);
    }

    public Gallery createGalleryItem(Gallery gallery) {
        return galleryRepository.save(gallery);
    }

    public Gallery updateGalleryItem(Long id, Gallery galleryDetails) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gallery item not found with id: " + id));

        gallery.setTitle(galleryDetails.getTitle());
        gallery.setDescription(galleryDetails.getDescription());
        gallery.setImageUrl(galleryDetails.getImageUrl());
        gallery.setActive(galleryDetails.isActive());

        return galleryRepository.save(gallery);
    }

    public void deleteGalleryItem(Long id) {
        galleryRepository.deleteById(id);
    }
}