// src/main/java/com/boatsafari/model/AboutContent.java
package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "about_content")
public class AboutContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_key", unique = true, nullable = false)
    private String sectionKey;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String subtitle;

    @Column(name = "description1", columnDefinition = "TEXT")
    private String description1;

    @Column(name = "description2", columnDefinition = "TEXT")
    private String description2;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public AboutContent() {}

    public AboutContent(String sectionKey, String title, String subtitle, String description1, String description2) {
        this.sectionKey = sectionKey;
        this.title = title;
        this.subtitle = subtitle;
        this.description1 = description1;
        this.description2 = description2;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSectionKey() { return sectionKey; }
    public void setSectionKey(String sectionKey) { this.sectionKey = sectionKey; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getDescription1() { return description1; }
    public void setDescription1(String description1) { this.description1 = description1; }
    public String getDescription2() { return description2; }
    public void setDescription2(String description2) { this.description2 = description2; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
