// src/main/java/com/boatsafari/controller/GalleryController.java
package com.boatsafari.controller;

import com.boatsafari.model.Gallery;
import com.boatsafari.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping
    public List<Gallery> getAllGalleryItems() {
        return galleryService.getAllGalleryItems();
    }

    @GetMapping("/active")
    public List<Gallery> getActiveGalleryItems() {
        return galleryService.getActiveGalleryItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gallery> getGalleryItemById(@PathVariable Long id) {
        Optional<Gallery> gallery = galleryService.getGalleryItemById(id);
        return gallery.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    public Gallery createGalleryItem(@RequestBody Gallery gallery) {
        return galleryService.createGalleryItem(gallery);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gallery> updateGalleryItem(@PathVariable Long id, @RequestBody Gallery galleryDetails) {
        try {
            Gallery updatedGallery = galleryService.updateGalleryItem(id, galleryDetails);
            return ResponseEntity.ok(updatedGallery);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGalleryItem(@PathVariable Long id) {
        galleryService.deleteGalleryItem(id);
        return ResponseEntity.ok().build();
    }
}