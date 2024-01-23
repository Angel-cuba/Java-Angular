package com.example.demo.controllers;

import com.example.demo.models.dto.reviews.ReviewRequest;
import com.example.demo.services.ReviewService;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping("/all/{postId}")
    public ResponseEntity<Map<String, Object>> getAllReviewsByPost(@PathVariable ObjectId postId) {
        return reviewService.getAllReviewsByPostId(postId);
    }
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<Map<String, Object>> getReviewById(@PathVariable String reviewId) {
        return reviewService.getReviewById(reviewId);
    }
    @PostMapping("/create/{id}")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody ReviewRequest reviewBody, @PathVariable ObjectId id) {
        return reviewService.createReview(reviewBody, id);
    }

    @PutMapping("/{postId}/update/{reviewId}/{userId}")
    public ResponseEntity<Map<String, Object>> updateReview(@PathVariable ObjectId postId, @RequestBody ReviewRequest review,@PathVariable String reviewId , @PathVariable String userId) {
        return reviewService.updateReview(postId, review, reviewId, userId);
    }

    @DeleteMapping("/{postId}/delete/{reviewId}/{userId}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable ObjectId postId, @PathVariable String reviewId, @PathVariable String userId) {
        return reviewService.deleteReview(postId, reviewId, userId);
    }
}
