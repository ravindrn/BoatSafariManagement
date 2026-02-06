package com.boatsafari.controller;

import com.boatsafari.dto.BoatDTO;
import com.boatsafari.model.Boat;
import com.boatsafari.service.BoatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/boats")
@CrossOrigin(origins = "*")
public class BoatController {

    @Autowired
    private BoatService boatService;

    // Get all boats
    @GetMapping
    public ResponseEntity<List<Boat>> getAllBoats() {
        List<Boat> boats = boatService.getAllBoats();
        return ResponseEntity.ok(boats);
    }

    // Get all boats as DTOs
    @GetMapping("/dto")
    public ResponseEntity<List<BoatDTO>> getAllBoatsAsDTO() {
        List<Boat> boats = boatService.getAllBoats();
        List<BoatDTO> boatDTOs = boatService.convertToDTOList(boats);
        return ResponseEntity.ok(boatDTOs);
    }

    // Get boat by ID
    @GetMapping("/{id}")
    public ResponseEntity<Boat> getBoatById(@PathVariable Long id) {
        Optional<Boat> boat = boatService.getBoatById(id);
        return boat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get boat by ID as DTO
    @GetMapping("/{id}/dto")
    public ResponseEntity<BoatDTO> getBoatByIdAsDTO(@PathVariable Long id) {
        Optional<Boat> boat = boatService.getBoatById(id);
        return boat.map(b -> ResponseEntity.ok(boatService.convertToDTO(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Get available boats
    @GetMapping("/available")
    public ResponseEntity<List<Boat>> getAvailableBoats() {
        List<Boat> boats = boatService.getAvailableBoats();
        return ResponseEntity.ok(boats);
    }

    // Get available boats for a specific date
    @GetMapping("/available/{date}")
    public ResponseEntity<List<Boat>> getAvailableBoatsForDate(@PathVariable String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            List<Boat> boats = boatService.getAvailableBoatsForDate(dateTime);
            return ResponseEntity.ok(boats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get boats by owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Boat>> getBoatsByOwner(@PathVariable Long ownerId) {
        List<Boat> boats = boatService.getBoatsByOwner(ownerId);
        return ResponseEntity.ok(boats);
    }

    // Get boats by type
    @GetMapping("/type/{boatType}")
    public ResponseEntity<List<Boat>> getBoatsByType(@PathVariable String boatType) {
        List<Boat> boats = boatService.getBoatsByType(boatType);
        return ResponseEntity.ok(boats);
    }

    // Get boats by status - FIXED: Use Boat.BoatStatus
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Boat>> getBoatsByStatus(@PathVariable Boat.BoatStatus status) {
        List<Boat> boats = boatService.getBoatsByStatus(status);
        return ResponseEntity.ok(boats);
    }

    // Get boats by approval status
    @GetMapping("/approval-status/{approvalStatus}")
    public ResponseEntity<List<Boat>> getBoatsByApprovalStatus(@PathVariable Boat.ApprovalStatus approvalStatus) {
        List<Boat> boats = boatService.getBoatsByApprovalStatus(approvalStatus);
        return ResponseEntity.ok(boats);
    }

    // Search boats
    @GetMapping("/search")
    public ResponseEntity<List<Boat>> searchBoats(@RequestParam String q) {
        List<Boat> boats = boatService.searchBoats(q);
        return ResponseEntity.ok(boats);
    }

    // Get boats by minimum capacity
    @GetMapping("/capacity/{minCapacity}")
    public ResponseEntity<List<Boat>> getBoatsByMinCapacity(@PathVariable Integer minCapacity) {
        List<Boat> boats = boatService.getBoatsByMinCapacity(minCapacity);
        return ResponseEntity.ok(boats);
    }

    // Create new boat (original JSON endpoint)
    @PostMapping
    public ResponseEntity<Boat> createBoat(@RequestBody Boat boat) {
        try {
            Boat createdBoat = boatService.createBoat(boat);
            return ResponseEntity.ok(createdBoat);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Create new boat with image upload
    @PostMapping(value = "/with-image", consumes = "multipart/form-data")
    public ResponseEntity<?> createBoatWithImage(
            @RequestParam("boatName") String boatName,
            @RequestParam("boatType") String boatType,
            @RequestParam("capacity") Integer capacity,
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestParam(value = "hourlyRate", required = false) Double hourlyRate,
            @RequestParam(value = "status", required = false) Boat.BoatStatus status,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "amenities", required = false) String amenities,
            @RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Boat boat = new Boat();
            boat.setBoatName(boatName);
            boat.setBoatType(boatType);
            boat.setCapacity(capacity);
            boat.setRegistrationNumber(registrationNumber);
            boat.setHourlyRate(hourlyRate != null ? hourlyRate : 50.00); // Default value
            boat.setStatus(status != null ? status : Boat.BoatStatus.AVAILABLE);
            boat.setDescription(description);
            boat.setAmenities(amenities);
            boat.setOwnerId(ownerId);

            Boat createdBoat = boatService.createBoatWithImage(boat, imageFile);
            return ResponseEntity.ok(createdBoat);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Update boat (original JSON endpoint)
    @PutMapping("/{id}")
    public ResponseEntity<Boat> updateBoat(@PathVariable Long id, @RequestBody Boat boat) {
        try {
            Boat updatedBoat = boatService.updateBoat(id, boat);
            return ResponseEntity.ok(updatedBoat);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update boat with image upload
    @PutMapping(value = "/{id}/with-image", consumes = "multipart/form-data")
    public ResponseEntity<?> updateBoatWithImage(
            @PathVariable Long id,
            @RequestParam("boatName") String boatName,
            @RequestParam("boatType") String boatType,
            @RequestParam("capacity") Integer capacity,
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestParam(value = "hourlyRate", required = false) Double hourlyRate,
            @RequestParam(value = "status", required = false) Boat.BoatStatus status,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "amenities", required = false) String amenities,
            @RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Boat boatDetails = new Boat();
            boatDetails.setBoatName(boatName);
            boatDetails.setBoatType(boatType);
            boatDetails.setCapacity(capacity);
            boatDetails.setRegistrationNumber(registrationNumber);
            boatDetails.setHourlyRate(hourlyRate);
            boatDetails.setStatus(status);
            boatDetails.setDescription(description);
            boatDetails.setAmenities(amenities);
            boatDetails.setOwnerId(ownerId);

            Boat updatedBoat = boatService.updateBoatWithImage(id, boatDetails, imageFile);
            return ResponseEntity.ok(updatedBoat);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Update boat image only
    @PatchMapping(value = "/{id}/image")
    public ResponseEntity<?> updateBoatImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile) {

        try {
            Boat updatedBoat = boatService.updateBoatImage(id, imageFile);
            return ResponseEntity.ok(updatedBoat);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Remove boat image
    @DeleteMapping("/{id}/image")
    public ResponseEntity<?> removeBoatImage(@PathVariable Long id) {
        try {
            Boat updatedBoat = boatService.removeBoatImage(id);
            return ResponseEntity.ok(updatedBoat);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to remove image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Update boat status - FIXED: Use Boat.BoatStatus
    @PatchMapping("/{id}/status")
    public ResponseEntity<Boat> updateBoatStatus(@PathVariable Long id, @RequestBody Boat.BoatStatus status) {
        try {
            Boat updatedBoat = boatService.updateBoatStatus(id, status);
            return ResponseEntity.ok(updatedBoat);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update boat approval status
    @PatchMapping("/{id}/approval")
    public ResponseEntity<Boat> updateBoatApproval(
            @PathVariable Long id,
            @RequestParam Boat.ApprovalStatus approvalStatus,
            @RequestParam(required = false) String approvalNotes,
            @RequestParam Long approvedBy) {
        try {
            Boat updatedBoat = boatService.updateBoatApproval(id, approvalStatus, approvalNotes, approvedBy);
            return ResponseEntity.ok(updatedBoat);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete boat
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoat(@PathVariable Long id) {
        try {
            boatService.deleteBoat(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Check if registration number exists
    @GetMapping("/check-registration")
    public ResponseEntity<Boolean> checkRegistrationNumberExists(@RequestParam String registrationNumber) {
        boolean exists = boatService.registrationNumberExists(registrationNumber);
        return ResponseEntity.ok(exists);
    }

    // Check if registration number exists excluding current boat
    @GetMapping("/check-registration/{excludeId}")
    public ResponseEntity<Boolean> checkRegistrationNumberExistsExcluding(
            @RequestParam String registrationNumber,
            @PathVariable Long excludeId) {
        boolean exists = boatService.registrationNumberExists(registrationNumber, excludeId);
        return ResponseEntity.ok(exists);
    }
}