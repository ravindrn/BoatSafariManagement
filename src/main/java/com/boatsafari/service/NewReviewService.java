// src/main/java/com/boatsafari/service/NewReviewService.java
package com.boatsafari.service;

import com.boatsafari.model.NewReview;
import com.boatsafari.model.ReviewStatus;
import com.boatsafari.repository.NewReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewReviewService {

    @Autowired
    private NewReviewRepository newReviewRepository;

    public List<NewReview> getAllNewReviews() {
        return newReviewRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<NewReview> getApprovedNewReviews() {
        return newReviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.APPROVED);
    }

    public List<NewReview> getPendingNewReviews() {
        return newReviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.PENDING);
    }

    public List<NewReview> getRejectedNewReviews() {
        return newReviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.REJECTED);
    }

    public Optional<NewReview> getNewReviewById(Long id) {
        return newReviewRepository.findById(id);
    }

    public NewReview createNewReview(NewReview review) {
        if (review.getStatus() == null) {
            review.setStatus(ReviewStatus.PENDING);
        }
        return newReviewRepository.save(review);
    }

    public NewReview approveNewReview(Long id) {
        NewReview review = newReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        review.setStatus(ReviewStatus.APPROVED);
        return newReviewRepository.save(review);
    }

    public NewReview rejectNewReview(Long id) {
        NewReview review = newReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        review.setStatus(ReviewStatus.REJECTED);
        return newReviewRepository.save(review);
    }

    public NewReview moveToPending(Long id) {
        NewReview review = newReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        review.setStatus(ReviewStatus.PENDING);
        return newReviewRepository.save(review);
    }

    public NewReview toggleHomepageVisibility(Long id, Boolean displayOnHomepage) {
        NewReview review = newReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        if (displayOnHomepage != null && displayOnHomepage) {
            review.setStatus(ReviewStatus.APPROVED);
        } else {
            review.setStatus(ReviewStatus.REJECTED);
        }

        return newReviewRepository.save(review);
    }

    public void deleteNewReview(Long id) {
        if (!newReviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found with id: " + id);
        }
        newReviewRepository.deleteById(id);
    }

    public long getPendingReviewsCount() {
        return newReviewRepository.countByStatus(ReviewStatus.PENDING);
    }

    public long getApprovedReviewsCount() {
        return newReviewRepository.countByStatus(ReviewStatus.APPROVED);
    }
}