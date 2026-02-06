// src/main/java/com/boatsafari/model/NewReview.java
package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "new_reviews")
public class NewReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "trip_name", nullable = false)
    private String tripName;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public NewReview() {}

    public NewReview(String customerName, String tripName, Integer rating, String comment) {
        this.customerName = customerName;
        this.tripName = tripName;
        this.rating = rating;
        this.comment = comment;
        this.status = ReviewStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public NewReview(String customerName, String tripName, Integer rating, String comment, ReviewStatus status) {
        this.customerName = customerName;
        this.tripName = tripName;
        this.rating = rating;
        this.comment = comment;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getTripName() { return tripName; }
    public void setTripName(String tripName) { this.tripName = tripName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper method for frontend compatibility
    public boolean isDisplayOnHomepage() {
        return status == ReviewStatus.APPROVED;
    }

    @Override
    public String toString() {
        return "NewReview{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", tripName='" + tripName + '\'' +
                ", rating=" + rating +
                ", status=" + status +
                '}';
    }
}