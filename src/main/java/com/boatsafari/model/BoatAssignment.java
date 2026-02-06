package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "boat_assignments")
public class BoatAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boat_id", nullable = false)
    private Boat boat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    @Column(name = "assignment_date")
    private LocalDateTime assignmentDate;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public BoatAssignment() {
        this.assignmentDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public BoatAssignment(Booking booking, Boat boat, User assignedBy) {
        this();
        this.booking = booking;
        this.boat = boat;
        this.assignedBy = assignedBy;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Boat getBoat() { return boat; }
    public void setBoat(Boat boat) { this.boat = boat; }

    public User getAssignedBy() { return assignedBy; }
    public void setAssignedBy(User assignedBy) { this.assignedBy = assignedBy; }

    public LocalDateTime getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(LocalDateTime assignmentDate) { this.assignmentDate = assignmentDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (assignmentDate == null) {
            assignmentDate = LocalDateTime.now();
        }
    }
}