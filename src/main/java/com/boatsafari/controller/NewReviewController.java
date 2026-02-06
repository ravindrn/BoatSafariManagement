// src/main/java/com/boatsafari/controller/NewReviewController.java
package com.boatsafari.controller;

import com.boatsafari.model.NewReview;
import com.boatsafari.service.NewReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/new-reviews")
public class NewReviewController {

    @Autowired
    private NewReviewService newReviewService;

    @GetMapping
    public List<NewReview> getAllNewReviews() {
        return newReviewService.getAllNewReviews();
    }

    @GetMapping("/approved")
    public List<NewReview> getApprovedNewReviews() {
        return newReviewService.getApprovedNewReviews();
    }

    @GetMapping("/pending")
    public List<NewReview> getPendingNewReviews() {
        return newReviewService.getPendingNewReviews();
    }

    @GetMapping("/rejected")
    public List<NewReview> getRejectedNewReviews() {
        return newReviewService.getRejectedNewReviews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewReview> getNewReviewById(@PathVariable Long id) {
        Optional<NewReview> review = newReviewService.getNewReviewById(id);
        return review.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createNewReview(@RequestBody NewReview review) {
        try {
            NewReview createdReview = newReviewService.createNewReview(review);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review submitted successfully");
            response.put("data", createdReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error submitting review: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveNewReview(@PathVariable Long id) {
        try {
            NewReview approvedReview = newReviewService.approveNewReview(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review approved successfully");
            response.put("data", approvedReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error approving review: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectNewReview(@PathVariable Long id) {
        try {
            NewReview rejectedReview = newReviewService.rejectNewReview(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review rejected successfully");
            response.put("data", rejectedReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error rejecting review: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/pending")
    public ResponseEntity<Map<String, Object>> moveToPending(@PathVariable Long id) {
        try {
            NewReview pendingReview = newReviewService.moveToPending(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review moved to pending successfully");
            response.put("data", pendingReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error moving review to pending: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/toggle-visibility")
    public ResponseEntity<Map<String, Object>> toggleHomepageVisibility(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean displayOnHomepage = request.get("displayOnHomepage");
            NewReview updatedReview = newReviewService.toggleHomepageVisibility(id, displayOnHomepage);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review visibility updated successfully");
            response.put("data", updatedReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error updating review visibility: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNewReview(@PathVariable Long id) {
        try {
            newReviewService.deleteNewReview(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting review: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Long>> getReviewCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("pending", newReviewService.getPendingReviewsCount());
        counts.put("approved", newReviewService.getApprovedReviewsCount());
        counts.put("total", (long) newReviewService.getAllNewReviews().size());
        return ResponseEntity.ok(counts);
    }
}