// src/main/java/com/boatsafari/model/Trip.java
package com.boatsafari.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Add this relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boat_id")
    private Boat boat;

    @Column(nullable = false)
    private String name;



    @Column(nullable = false)
    private String type;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private BigDecimal pricePerPerson;

    @Column(nullable = false)
    private String destination;

    private String imageUrl;
    private boolean featured = false;
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Trip() {}

    public Trip(String name, String type, String description, Integer duration,
                Integer capacity, BigDecimal pricePerPerson, String destination) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.duration = duration;
        this.capacity = capacity;
        this.pricePerPerson = pricePerPerson;
        this.destination = destination;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public BigDecimal getPricePerPerson() { return pricePerPerson; }
    public void setPricePerPerson(BigDecimal pricePerPerson) { this.pricePerPerson = pricePerPerson; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
