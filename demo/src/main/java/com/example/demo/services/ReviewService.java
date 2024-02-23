package com.example.demo.services;

import com.example.demo.models.Post;
import com.example.demo.models.Review;
import com.example.demo.models.dto.reviews.ReviewRequest;
import com.example.demo.models.dto.reviews.ReviewResponse;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.utils.Helpers;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final Helpers helpers;
    String notFound = "Not found";
    String revId = "reviewIds";

    public ReviewService(ReviewRepository reviewRepository, PostRepository postRepository, MongoTemplate mongoTemplate, Helpers helpers) {
        this.reviewRepository = reviewRepository;
        this.postRepository = postRepository;
        this.mongoTemplate = mongoTemplate;
        this.helpers = helpers;
    }

    private Object mapReviewToReviewResponse(Review review) {
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setId(String.valueOf(review.getId()));
        reviewResponse.setBody(review.getBody());
        reviewResponse.setAuthorId(review.getAuthorId());
        reviewResponse.setCreatedAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        reviewResponse.setUpdatedAt(review.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return reviewResponse;
    }

    private boolean isReviewValid(@NotNull ReviewRequest reviewBody) {
        return reviewBody.getBody() == null || reviewBody.getBody().isEmpty() || reviewBody.getAuthorId() == null || reviewBody.getAuthorId().isEmpty();
    }

    public ResponseEntity<Map<String, Object>> getAllReviewsByPostId(@PathVariable ObjectId id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }

        List<String> reviewIds = post.get().getReviewIds();
        if (reviewIds.isEmpty()) {
            return helpers.response("No reviews found", HttpStatus.OK);
        }
        List<Object> reviews = new ArrayList<>();
        for (String reviewId : reviewIds) {
            Optional<Review> review = reviewRepository.findById(new ObjectId(reviewId));
            if (review.isEmpty()) {
                return helpers.response(notFound, HttpStatus.NOT_FOUND);
            }
            Object o = mapReviewToReviewResponse(review.get());
            reviews.add(o);
        }

        return helpers.responseWithData("Reviews retrieved successfully", HttpStatus.OK, reviews);
    }

    public ResponseEntity<Map<String, Object>> getReviewById(@PathVariable String id) {
        Optional<Review> review = reviewRepository.findById(new ObjectId(id));
        if (review.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        return helpers.responseWithData("Review retrieved successfully", HttpStatus.OK, review);
    }

    public ResponseEntity<Map<String, Object>> createReview(@RequestBody ReviewRequest reviewBody, @PathVariable ObjectId postId) {
        if (isReviewValid(reviewBody)) {
            return helpers.response("Invalid review", HttpStatus.BAD_REQUEST);
        }
        Review newReview = new Review();
        newReview.setBody(reviewBody.getBody());
        newReview.setAuthorId(reviewBody.getAuthorId());
        newReview.setCreatedAt(LocalDateTime.now());
        newReview.setUpdatedAt(LocalDateTime.now());

        Review review = reviewRepository.insert(newReview);
        mongoTemplate.update(Post.class)
                .matching(Criteria.where("_id").is(postId))
                .apply((new Update().push(revId, review.getId()))
                ).first();
        return helpers.responseWithData("Review created successfully", HttpStatus.CREATED, review.getBody());
    }

    public ResponseEntity<Map<String, Object>> updateReview(@PathVariable ObjectId postId, @RequestBody ReviewRequest review, @PathVariable String reviewId, @PathVariable String userId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        Optional<Review> reviewToUpdate = reviewRepository.findById(new ObjectId(reviewId));
        if (reviewToUpdate.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        if (!reviewToUpdate.get().getAuthorId().equals(userId)) {
            return helpers.response("You are not authorized to update this review", HttpStatus.UNAUTHORIZED);
        }
        mongoTemplate.update(Review.class)
                .matching(Criteria.where("_id").is(reviewId))
                .apply((new Update().set("body", review.getBody()).set("updatedAt", LocalDateTime.now()))
                ).first();
        Optional<Review> updatedReview = reviewRepository.findById(new ObjectId(reviewId));
        if (updatedReview.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        return helpers.responseWithData("Review updated successfully", HttpStatus.OK, updatedReview.get().getBody());
    }

    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable ObjectId postId, @PathVariable String reviewId, @PathVariable String userId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        Optional<Review> reviewToDelete = reviewRepository.findById(new ObjectId(reviewId));
        if (reviewToDelete.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        if (!reviewToDelete.get().getAuthorId().equals(userId)) {
            return helpers.response("You are not authorized to delete this review", HttpStatus.UNAUTHORIZED);
        }
        mongoTemplate.update(Post.class)
                .matching(Criteria.where("_id").is(postId))
                .apply((new Update().pull(revId, reviewId))
                ).first();

        reviewRepository.deleteById(new ObjectId(reviewId));

        mongoTemplate.update(Post.class)
                .matching(Criteria.where("_id").is(postId))
                .apply((new Update().pull(revId, reviewId))
                ).first();
        return helpers.response("Review deleted successfully", HttpStatus.OK);
    }
}