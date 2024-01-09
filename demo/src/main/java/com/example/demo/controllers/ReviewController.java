package com.example.demo.controllers;

import com.example.demo.models.dto.Reviews.ReviewRequest;
import com.example.demo.services.ReviewService;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody ReviewRequest reviewBody, @PathVariable ObjectId id) {
        return reviewService.createReview(reviewBody, id);
    }

    @PutMapping("/{postId}/update/{reviewId}/{userId}")
    public ResponseEntity<Map<String, Object>> updateReview(@PathVariable ObjectId postId, @RequestBody ReviewRequest review,@PathVariable String reviewId , @PathVariable String userId) {
        return reviewService.updateReview(postId, review, reviewId, userId);
    }
}
