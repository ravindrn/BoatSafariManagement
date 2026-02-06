package com.boatsafari.dto;

import java.time.LocalDateTime;

public class BoatAssignmentResponseDTO {
    private Long id;
    private Long bookingId;
    private BoatDTO boat;
    private Long assignedById;
    private String assignedByName;
    private LocalDateTime assignmentDate;
    private String notes;
    private LocalDateTime createdAt;

    // Constructors
    public BoatAssignmentResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public BoatDTO getBoat() { return boat; }
    public void setBoat(BoatDTO boat) { this.boat = boat; }

    public Long getAssignedById() { return assignedById; }
    public void setAssignedById(Long assignedById) { this.assignedById = assignedById; }

    public String getAssignedByName() { return assignedByName; }
    public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }

    public LocalDateTime getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(LocalDateTime assignmentDate) { this.assignmentDate = assignmentDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}