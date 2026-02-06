package com.boatsafari.dto;

import com.boatsafari.model.Boat;
import java.time.LocalDateTime;

public class BoatDTO {
    private Long id;
    private String boatName;
    private String boatType;
    private Integer capacity;
    private String registrationNumber;
    private Boat.BoatStatus status;
    private Double hourlyRate;
    private String description;
    private String imageUrl;
    private String amenities;
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private Boat.ApprovalStatus approvalStatus;
    private LocalDateTime approvalDate;
    private String approvalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public BoatDTO() {}

    public BoatDTO(Long id, String boatName, String boatType, Integer capacity, String registrationNumber,
                   Boat.BoatStatus status, Double hourlyRate, String description, String imageUrl,
                   String amenities, Long ownerId, String ownerName, String ownerEmail,
                   Boat.ApprovalStatus approvalStatus, LocalDateTime approvalDate, String approvalNotes,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.boatName = boatName;
        this.boatType = boatType;
        this.capacity = capacity;
        this.registrationNumber = registrationNumber;
        this.status = status;
        this.hourlyRate = hourlyRate;
        this.description = description;
        this.imageUrl = imageUrl;
        this.amenities = amenities;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.approvalStatus = approvalStatus;
        this.approvalDate = approvalDate;
        this.approvalNotes = approvalNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBoatName() { return boatName; }
    public void setBoatName(String boatName) { this.boatName = boatName; }

    public String getBoatType() { return boatType; }
    public void setBoatType(String boatType) { this.boatType = boatType; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Boat.BoatStatus getStatus() { return status; }
    public void setStatus(Boat.BoatStatus status) { this.status = status; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public Boat.ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(Boat.ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public String getApprovalNotes() { return approvalNotes; }
    public void setApprovalNotes(String approvalNotes) { this.approvalNotes = approvalNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    private String imageFilename;

    // Add getter and setter:
    public String getImageFilename() { return imageFilename; }
    public void setImageFilename(String imageFilename) { this.imageFilename = imageFilename; }
}