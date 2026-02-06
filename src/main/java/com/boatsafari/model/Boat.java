// Boat.java - Update the constructors
package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "boats")
public class Boat {

    public enum BoatStatus {
        AVAILABLE, MAINTENANCE, IN_USE, INACTIVE
    }

    public enum ApprovalStatus {
        APPROVED, PENDING, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "boat_name", nullable = false)
    private String boatName;

    @Column(name = "boat_type", nullable = false)
    private String boatType;

    @Column(nullable = false)
    private Integer capacity;

    // CHANGED: Using direct owner_id reference
    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "registration_number", unique = true, nullable = false)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoatStatus status = BoatStatus.AVAILABLE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "amenities")
    private String amenities;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approval_notes")
    private String approvalNotes;

    @Column(name = "approved_by")
    private Long approvedBy;

    // In your Boat entity, add this field:
    @Column(name = "image_filename")
    private String imageFilename;



    // Constructors - UPDATED
    public Boat() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    // Constructor without owner parameter
    public Boat(String boatName, String boatType, Integer capacity, String registrationNumber) {
        this();
        this.boatName = boatName;
        this.boatType = boatType;
        this.capacity = capacity;
        this.registrationNumber = registrationNumber;
    }

    // Constructor with ownerId
    public Boat(String boatName, String boatType, Integer capacity, String registrationNumber, Long ownerId) {
        this();
        this.boatName = boatName;
        this.boatType = boatType;
        this.capacity = capacity;
        this.registrationNumber = registrationNumber;
        this.ownerId = ownerId;
    }

    // Getters and Setters (keep all existing ones)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBoatName() { return boatName; }
    public void setBoatName(String boatName) { this.boatName = boatName; }

    // Update getters and setters
    public String getImageFilename() { return imageFilename; }
    public void setImageFilename(String imageFilename) { this.imageFilename = imageFilename; }

    public String getBoatType() { return boatType; }
    public void setBoatType(String boatType) { this.boatType = boatType; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public BoatStatus getStatus() { return status; }
    public void setStatus(BoatStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public String getApprovalNotes() { return approvalNotes; }
    public void setApprovalNotes(String approvalNotes) { this.approvalNotes = approvalNotes; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();

        if (approvalStatus == null) {
            approvalStatus = ApprovalStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}