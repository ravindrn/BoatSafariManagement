// src/main/java/com/boatsafari/model/AboutStats.java
package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "about_stats")
public class AboutStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_key", unique = true, nullable = false)
    private String key;

    @Column(name = "stat_value", nullable = false)
    private String value;

    @Column(name = "stat_label", nullable = false)
    private String label;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public AboutStats() {}

    public AboutStats(String key, String value, String label) {
        this.key = key;
        this.value = value;
        this.label = label;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}