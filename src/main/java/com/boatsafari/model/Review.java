// src/main/java/com/boatsafari/model/Review.java
package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000, nullable = false)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Review() {}

    public Review(String customerName, Integer rating, String comment, Trip trip) {
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.trip = trip;
        this.status = ReviewStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Review(String customerName, Integer rating, String comment, Trip trip, ReviewStatus status) {
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.trip = trip;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    @JsonProperty("customerName")
    public String getCustomerName() { return customerName; }

    @JsonProperty("customer_name")
    public String getCustomer_name() { return customerName; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }

    // For backward compatibility with existing code
    @JsonProperty("approved")
    public boolean isApproved() {
        return status == ReviewStatus.APPROVED;
    }

    public void setApproved(boolean approved) {
        this.status = approved ? ReviewStatus.APPROVED : ReviewStatus.PENDING;
    }

    // Helper method for frontend compatibility
    @JsonProperty("display_on_homepage")
    public boolean isDisplayOnHomepage() {
        return status == ReviewStatus.APPROVED;
    }

    @JsonProperty("trip_name")
    public String getTripName() {
        return trip != null ? trip.getName() : "N/A";
    }

    @JsonProperty("createdAt")
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", rating=" + rating +
                ", status=" + status +
                ", trip=" + (trip != null ? trip.getName() : "null") +
                '}';
    }
}