// src/main/java/com/boatsafari/service/ReviewService.java
package com.boatsafari.service;

import com.boatsafari.model.Review;
import com.boatsafari.model.ReviewStatus;
import com.boatsafari.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Review> getApprovedReviews() {
        return reviewRepository.findByStatus(ReviewStatus.APPROVED);
    }

    public List<Review> getPendingReviews() {
        return reviewRepository.findByStatus(ReviewStatus.PENDING);
    }

    public List<Review> getReviewsByTripId(Long tripId) {
        return reviewRepository.findByTripIdAndStatus(tripId, ReviewStatus.APPROVED);
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public Review createReview(Review review) {
        // Set default status to PENDING if not provided
        if (review.getStatus() == null) {
            review.setStatus(ReviewStatus.PENDING);
        }
        return reviewRepository.save(review);
    }

    public Review approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        review.setStatus(ReviewStatus.APPROVED);
        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public double getAverageRatingForTrip(Long tripId) {
        List<Review> reviews = reviewRepository.findByTripIdAndStatus(tripId, ReviewStatus.APPROVED);
        if (reviews.isEmpty()) return 0.0;

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public Review toggleHomepageVisibility(Long id, Boolean displayOnHomepage) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        review.setStatus(displayOnHomepage != null && displayOnHomepage ?
                ReviewStatus.APPROVED : ReviewStatus.PENDING);

        return reviewRepository.save(review);
    }

    // Additional helper methods
    public long getPendingReviewsCount() {
        return reviewRepository.countByStatus(ReviewStatus.PENDING);
    }

    public long getApprovedReviewsCount() {
        return reviewRepository.countByStatus(ReviewStatus.APPROVED);
    }
}