// src/main/java/com/boatsafari/repository/ReviewRepository.java
package com.boatsafari.repository;

import com.boatsafari.model.Review;
import com.boatsafari.model.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // New methods using status enum
    List<Review> findByStatus(ReviewStatus status);
    List<Review> findByStatusOrderByCreatedAtDesc(ReviewStatus status);
    List<Review> findAllByOrderByCreatedAtDesc();
    List<Review> findByTripIdAndStatus(Long tripId, ReviewStatus status);
    List<Review> findByTripId(Long tripId);
    long countByStatus(ReviewStatus status);

    // Remove these old methods that use 'approved' field:
    // List<Review> findByApprovedTrue();
    // List<Review> findByApprovedFalse();
    // List<Review> findByTripIdAndApprovedTrue(Long tripId);
}