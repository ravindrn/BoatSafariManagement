// src/main/java/com/boatsafari/controller/ReviewController.java
package com.boatsafari.controller;

import com.boatsafari.model.Review;
import com.boatsafari.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/approved")
    public List<Review> getApprovedReviews() {
        return reviewService.getApprovedReviews();
    }

    @GetMapping("/pending")
    public List<Review> getPendingReviews() {
        return reviewService.getPendingReviews();
    }

    @GetMapping("/trip/{tripId}")
    public List<Review> getReviewsByTripId(@PathVariable Long tripId) {
        return reviewService.getReviewsByTripId(tripId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable Long id) {
        try {
            Review approvedReview = reviewService.approveReview(id);
            return ResponseEntity.ok(approvedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trip/{tripId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long tripId) {
        double averageRating = reviewService.getAverageRatingForTrip(tripId);
        return ResponseEntity.ok(averageRating);
    }
}