// src/main/java/com/boatsafari/repository/NewReviewRepository.java
package com.boatsafari.repository;

import com.boatsafari.model.NewReview;
import com.boatsafari.model.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewReviewRepository extends JpaRepository<NewReview, Long> {
    List<NewReview> findByStatus(ReviewStatus status);
    List<NewReview> findByStatusOrderByCreatedAtDesc(ReviewStatus status);
    List<NewReview> findAllByOrderByCreatedAtDesc();
    long countByStatus(ReviewStatus status);
}