package com.boatsafari.service;

import com.boatsafari.dto.BoatDTO;
import com.boatsafari.model.Boat;
import com.boatsafari.model.User;
import com.boatsafari.repository.BoatRepository;
import com.boatsafari.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoatService {

    private static final Logger logger = LoggerFactory.getLogger(BoatService.class);

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // Get all boats
    public List<Boat> getAllBoats() {
        return boatRepository.findAll();
    }

    // Get boat by ID
    public Optional<Boat> getBoatById(Long id) {
        return boatRepository.findById(id);
    }

    // Get available boats
    public List<Boat> getAvailableBoats() {
        return boatRepository.findByStatus(Boat.BoatStatus.AVAILABLE);
    }

    // Get available boats for a specific date
    public List<Boat> getAvailableBoatsForDate(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        return boatRepository.findAvailableBoatsForDate(startOfDay, endOfDay);
    }

    // Create new boat (original method)
    public Boat createBoat(Boat boat) {
        logger.info("Creating boat: {}", boat.getBoatName());

        // Check if registration number already exists
        if (boatRepository.findByRegistrationNumber(boat.getRegistrationNumber()).isPresent()) {
            throw new RuntimeException("Boat with registration number " + boat.getRegistrationNumber() + " already exists");
        }

        // Set default values if not provided
        if (boat.getStatus() == null) {
            boat.setStatus(Boat.BoatStatus.AVAILABLE);
        }
        if (boat.getApprovalStatus() == null) {
            boat.setApprovalStatus(Boat.ApprovalStatus.PENDING);
        }
        if (boat.getCreatedAt() == null) {
            boat.setCreatedAt(LocalDateTime.now());
        }
        boat.setUpdatedAt(LocalDateTime.now());

        Boat savedBoat = boatRepository.save(boat);
        logger.info("Boat created successfully with ID: {}", savedBoat.getId());
        return savedBoat;
    }

    // Create new boat with image upload
    public Boat createBoatWithImage(Boat boat, MultipartFile imageFile) throws IOException {
        logger.info("Creating boat with image: {}", boat.getBoatName());

        // Check if registration number already exists
        if (boatRepository.findByRegistrationNumber(boat.getRegistrationNumber()).isPresent()) {
            throw new RuntimeException("Boat with registration number " + boat.getRegistrationNumber() + " already exists");
        }

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            boat.setImageFilename(fileName);
            logger.info("Image uploaded successfully: {}", fileName);
        }

        // Set default values if not provided
        if (boat.getStatus() == null) {
            boat.setStatus(Boat.BoatStatus.AVAILABLE);
        }
        if (boat.getApprovalStatus() == null) {
            boat.setApprovalStatus(Boat.ApprovalStatus.PENDING);
        }
        if (boat.getCreatedAt() == null) {
            boat.setCreatedAt(LocalDateTime.now());
        }
        boat.setUpdatedAt(LocalDateTime.now());

        Boat savedBoat = boatRepository.save(boat);
        logger.info("Boat created successfully with ID: {}", savedBoat.getId());
        return savedBoat;
    }

    // Update boat (original method)
    public Boat updateBoat(Long id, Boat boatDetails) {
        logger.info("Updating boat: {}", id);

        Boat boat = boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boat not found with id: " + id));

        // Check if registration number is being changed and if it already exists
        if (boatDetails.getRegistrationNumber() != null &&
                !boatDetails.getRegistrationNumber().equals(boat.getRegistrationNumber())) {
            if (boatRepository.findByRegistrationNumber(boatDetails.getRegistrationNumber()).isPresent()) {
                throw new RuntimeException("Boat with registration number " + boatDetails.getRegistrationNumber() + " already exists");
            }
            boat.setRegistrationNumber(boatDetails.getRegistrationNumber());
        }

        // Update fields if provided
        if (boatDetails.getBoatName() != null) {
            boat.setBoatName(boatDetails.getBoatName());
        }
        if (boatDetails.getBoatType() != null) {
            boat.setBoatType(boatDetails.getBoatType());
        }
        if (boatDetails.getCapacity() != null) {
            boat.setCapacity(boatDetails.getCapacity());
        }
        if (boatDetails.getStatus() != null) {
            boat.setStatus(boatDetails.getStatus());
        }
        if (boatDetails.getHourlyRate() != null) {
            boat.setHourlyRate(boatDetails.getHourlyRate());
        }
        if (boatDetails.getDescription() != null) {
            boat.setDescription(boatDetails.getDescription());
        }
        if (boatDetails.getImageUrl() != null) {
            boat.setImageUrl(boatDetails.getImageUrl());
        }
        if (boatDetails.getAmenities() != null) {
            boat.setAmenities(boatDetails.getAmenities());
        }
        if (boatDetails.getOwnerId() != null) {
            boat.setOwnerId(boatDetails.getOwnerId());
        }

        // If important details changed, reset approval status
        if (boatDetails.getBoatName() != null || boatDetails.getBoatType() != null ||
                boatDetails.getCapacity() != null || boatDetails.getRegistrationNumber() != null) {
            boat.setApprovalStatus(Boat.ApprovalStatus.PENDING);
            boat.setApprovalDate(null);
            boat.setApprovalNotes(null);
            boat.setApprovedBy(null);
        }

        boat.setUpdatedAt(LocalDateTime.now());

        Boat updatedBoat = boatRepository.save(boat);
        logger.info("Boat updated successfully");
        return updatedBoat;
    }

    // Update boat with image upload
    public Boat updateBoatWithImage(Long id, Boat boatDetails, MultipartFile imageFile) throws IOException {
        logger.info("Updating boat with image: {}", id);

        Boat boat = boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boat not found with id: " + id));

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image if exists
            if (boat.getImageFilename() != null) {
                try {
                    fileStorageService.deleteFile(boat.getImageFilename());
                    logger.info("Old image deleted: {}", boat.getImageFilename());
                } catch (IOException e) {
                    logger.warn("Could not delete old image file: {}", e.getMessage());
                }
            }
            // Store new image
            String fileName = fileStorageService.storeFile(imageFile);
            boat.setImageFilename(fileName);
            logger.info("New image uploaded: {}", fileName);
        }

        // Check if registration number is being changed and if it already exists
        if (boatDetails.getRegistrationNumber() != null &&
                !boatDetails.getRegistrationNumber().equals(boat.getRegistrationNumber())) {
            if (boatRepository.findByRegistrationNumber(boatDetails.getRegistrationNumber()).isPresent()) {
                throw new RuntimeException("Boat with registration number " + boatDetails.getRegistrationNumber() + " already exists");
            }
            boat.setRegistrationNumber(boatDetails.getRegistrationNumber());
        }

        // Update fields if provided
        if (boatDetails.getBoatName() != null) {
            boat.setBoatName(boatDetails.getBoatName());
        }
        if (boatDetails.getBoatType() != null) {
            boat.setBoatType(boatDetails.getBoatType());
        }
        if (boatDetails.getCapacity() != null) {
            boat.setCapacity(boatDetails.getCapacity());
        }
        if (boatDetails.getStatus() != null) {
            boat.setStatus(boatDetails.getStatus());
        }
        if (boatDetails.getHourlyRate() != null) {
            boat.setHourlyRate(boatDetails.getHourlyRate());
        }
        if (boatDetails.getDescription() != null) {
            boat.setDescription(boatDetails.getDescription());
        }
        if (boatDetails.getImageUrl() != null) {
            boat.setImageUrl(boatDetails.getImageUrl());
        }
        if (boatDetails.getAmenities() != null) {
            boat.setAmenities(boatDetails.getAmenities());
        }
        if (boatDetails.getOwnerId() != null) {
            boat.setOwnerId(boatDetails.getOwnerId());
        }

        // If important details changed, reset approval status
        if (boatDetails.getBoatName() != null || boatDetails.getBoatType() != null ||
                boatDetails.getCapacity() != null || boatDetails.getRegistrationNumber() != null) {
            boat.setApprovalStatus(Boat.ApprovalStatus.PENDING);
            boat.setApprovalDate(null);
            boat.setApprovalNotes(null);
            boat.setApprovedBy(null);
        }

        boat.setUpdatedAt(LocalDateTime.now());

        Boat updatedBoat = boatRepository.save(boat);
        logger.info("Boat updated successfully with image");
        return updatedBoat;
    }

    // Update boat status
    public Boat updateBoatStatus(Long id, Boat.BoatStatus status) {
        logger.info("Updating boat {} status to: {}", id, status);

        Boat boat = boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boat not found with id: " + id));

        boat.setStatus(status);
        boat.setUpdatedAt(LocalDateTime.now());

        Boat updatedBoat = boatRepository.save(boat);
        logger.info("Boat status updated successfully");
        return updatedBoat;
    }

    // Update boat approval status
    public Boat updateBoatApproval(Long id, Boat.ApprovalStatus approvalStatus, String approvalNotes, Long approvedBy) {
        logger.info("Updating boat {} approval status to: {}", id, approvalStatus);

        Boat boat = boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boat not found with id: " + id));

        boat.setApprovalStatus(approvalStatus);
        boat.setApprovalNotes(approvalNotes);
        boat.setApprovedBy(approvedBy);

        if (approvalStatus == Boat.ApprovalStatus.APPROVED) {
            boat.setApprovalDate(LocalDateTime.now());
        }

        boat.setUpdatedAt(LocalDateTime.now());

        Boat updatedBoat = boatRepository.save(boat);
        logger.info("Boat approval status updated successfully");
        return updatedBoat;
    }

    // Delete boat
    public void deleteBoat(Long id) {
        logger.info("Deleting boat: {}", id);

        Boat boat = boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boat not found with id: " + id));

        // Delete associated image file if exists
        if (boat.getImageFilename() != null) {
            try {
                fileStorageService.deleteFile(boat.getImageFilename());
                logger.info("Image file deleted: {}", boat.getImageFilename());
            } catch (IOException e) {
                logger.warn("Could not delete image file: {}", e.getMessage());
            }
        }

        boatRepository.deleteById(id);
        logger.info("Boat deleted successfully");
    }

    // Get boats by owner
    public List<Boat> getBoatsByOwner(Long ownerId) {
        return boatRepository.findByOwnerId(ownerId);
    }

    // Get boats by type
    public List<Boat> getBoatsByType(String boatType) {
        return boatRepository.findByBoatType(boatType);
    }

    // Get boats by status
    public List<Boat> getBoatsByStatus(Boat.BoatStatus status) {
        return boatRepository.findByStatus(status);
    }

    // Get boats by approval status
    public List<Boat> getBoatsByApprovalStatus(Boat.ApprovalStatus approvalStatus) {
        return boatRepository.findByApprovalStatus(approvalStatus);
    }

    // Search boats by name or registration number
    public List<Boat> searchBoats(String searchTerm) {
        return boatRepository.findByBoatNameContainingIgnoreCaseOrRegistrationNumberContainingIgnoreCase(searchTerm);
    }

    // In BoatService, update the convertToDTO method:
    public BoatDTO convertToDTO(Boat boat) {
        BoatDTO dto = new BoatDTO();
        dto.setId(boat.getId());
        dto.setBoatName(boat.getBoatName());
        dto.setBoatType(boat.getBoatType());
        dto.setCapacity(boat.getCapacity());
        dto.setRegistrationNumber(boat.getRegistrationNumber());
        dto.setStatus(boat.getStatus());
        dto.setHourlyRate(boat.getHourlyRate());
        dto.setDescription(boat.getDescription());
        dto.setImageUrl(boat.getImageUrl());
        dto.setAmenities(boat.getAmenities());
        dto.setOwnerId(boat.getOwnerId());
        dto.setApprovalStatus(boat.getApprovalStatus());
        dto.setApprovalDate(boat.getApprovalDate());
        dto.setApprovalNotes(boat.getApprovalNotes());
        dto.setCreatedAt(boat.getCreatedAt());
        dto.setUpdatedAt(boat.getUpdatedAt());

        // Add image filename to DTO
        if (boat.getImageFilename() != null) {
            dto.setImageFilename(boat.getImageFilename());
            // Use the stored filename to generate URL
            dto.setImageUrl(fileStorageService.getFileUrl(boat.getImageFilename()));
        }

        // If ownerId exists, try to get owner details
        if (boat.getOwnerId() != null) {
            dto.setOwnerId(boat.getOwnerId());
            Optional<User> owner = userRepository.findById(boat.getOwnerId());
            if (owner.isPresent()) {
                dto.setOwnerName(owner.get().getFirstName() + " " + owner.get().getLastName());
                dto.setOwnerEmail(owner.get().getEmail());
            }
        }

        return dto;
    }

    // Convert list of boats to DTOs
    public List<BoatDTO> convertToDTOList(List<Boat> boats) {
        return boats.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get boats with capacity greater than or equal to
    public List<Boat> getBoatsByMinCapacity(Integer minCapacity) {
        return boatRepository.findByCapacityGreaterThanEqual(minCapacity);
    }

    // Check if registration number exists
    public boolean registrationNumberExists(String registrationNumber) {
        return boatRepository.findByRegistrationNumber(registrationNumber).isPresent();
    }

    // Check if registration number exists excluding current boat
    public boolean registrationNumberExists(String registrationNumber, Long excludeBoatId) {
        return boatRepository.existsByRegistrationNumberAndIdNot(registrationNumber, excludeBoatId);
    }

    // Update only boat image
    public Boat updateBoatImage(Long id, MultipartFile imageFile) throws IOException {
        logger.info("Updating boat image: {}", id);

        Boat boat = boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boat not found with id: " + id));

        // Delete old image if exists
        if (boat.getImageFilename() != null) {
            try {
                fileStorageService.deleteFile(boat.getImageFilename());
                logger.info("Old image deleted: {}", boat.getImageFilename());
            } catch (IOException e) {
                logger.warn("Could not delete old image file: {}", e.getMessage());
            }
        }

        // Store new image
        String fileName = fileStorageService.storeFile(imageFile);
        boat.setImageFilename(fileName);
        boat.setUpdatedAt(LocalDateTime.now());

        Boat updatedBoat = boatRepository.save(boat);
        logger.info("Boat image updated successfully: {}", fileName);
        return updatedBoat;
    }

    // Remove boat image
    public Boat removeBoatImage(Long id) throws IOException {
        logger.info("Removing boat image: {}", id);

        Boat boat = boatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boat not found with id: " + id));

        if (boat.getImageFilename() != null) {
            fileStorageService.deleteFile(boat.getImageFilename());
            boat.setImageFilename(null);
            boat.setUpdatedAt(LocalDateTime.now());

            Boat updatedBoat = boatRepository.save(boat);
            logger.info("Boat image removed successfully");
            return updatedBoat;
        }

        logger.info("No image to remove for boat: {}", id);
        return boat;
    }
}