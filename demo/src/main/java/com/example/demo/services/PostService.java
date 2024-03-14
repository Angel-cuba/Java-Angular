package com.example.demo.services;

import com.example.demo.models.Post;
import com.example.demo.models.dto.posts.PostRequest;
import com.example.demo.models.dto.posts.PostUpdateRequest;
import com.example.demo.repository.PostRepository;
import com.example.demo.utils.Helpers;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final Helpers helpers;
    @Autowired
    public PostService(PostRepository postRepository, Helpers helpers) {
        this.postRepository = postRepository;
        this.helpers = helpers;
    }

    String notFound = "Post not found";

    private boolean isPostValid(@NotNull PostRequest postRequest) {
        return postRequest.getTitle() == null || postRequest.getTitle().isEmpty() || postRequest.getBody() == null || postRequest.getBody().isEmpty() || postRequest.getAuthorId() == null || postRequest.getAuthorId().isEmpty() || postRequest.getImage() == null || postRequest.getImage().isEmpty();
    }

    private boolean isPostValidForUpdate(@NotNull PostUpdateRequest post) {
        return post.getTitle() == null || post.getTitle().isEmpty() || post.getBody() == null || post.getBody().isEmpty() || post.getImage() == null || post.getImage().isEmpty();
    }

    public ResponseEntity<Map<String, Object>> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return helpers.responseWithData("Posts retrieved successfully", HttpStatus.OK, posts);
    }

    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable ObjectId postId) {
        if (!postRepository.existsById(postId)) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        else {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                return helpers.responseWithData("Post retrieved successfully", HttpStatus.OK, post.get());
            }
        }
        return helpers.response(notFound, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Map<String, Object>> getPostsByAuthorId(@PathVariable String authorId) {
        List<Post> filteredPosts = postRepository.findByAuthorId(authorId);
        if (filteredPosts.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        return helpers.responseWithData("Posts retrieved successfully", HttpStatus.OK, filteredPosts);
    }

    public ResponseEntity<Map<String, Object>> likePost(@PathVariable ObjectId id, @PathVariable String userId) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        Post postToLike = post.get();
        List<String> likes = postToLike.getLikes();
        // Check if user has already liked the post and delete the like if they have
        if (likes.contains(userId)) {
            likes.remove(userId);
            postToLike.setLikes(likes);
            postRepository.save(postToLike);
            return helpers.responseWithData("Post unliked successfully", HttpStatus.OK, postToLike);
        }
        likes.add(userId);
        postToLike.setLikes(likes);
        postRepository.save(postToLike);
        return helpers.responseWithData("Post liked successfully", HttpStatus.OK, postToLike);
    }

    public ResponseEntity<Map<String, Object>> createPost(@RequestBody PostRequest postRequest) {
        if (isPostValid(postRequest)) {
            return helpers.response("Invalid post", HttpStatus.BAD_REQUEST);
        }
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setBody(postRequest.getBody());
        post.setAuthorId(postRequest.getAuthorId());
        post.setImage(postRequest.getImage());
        post.setTags(postRequest.getTags() != null ? postRequest.getTags() : new ArrayList<>());
        post.setLikes(postRequest.getLikes() != null ? postRequest.getLikes() : new ArrayList<>());
        post.setReviewIds(postRequest.getReviewIds() != null ? postRequest.getReviewIds() : new ArrayList<>());
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return helpers.responseWithData("Post created successfully", HttpStatus.CREATED, savedPost.getId());
    }

    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable ObjectId postId, @PathVariable String userId, @RequestBody PostUpdateRequest post) {
        Optional<Post> postToUpdate = postRepository.findById(postId);
        String authorId;

        if (postToUpdate.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        if (isPostValidForUpdate(post)) {
            return helpers.response("Invalid post", HttpStatus.BAD_REQUEST);
        }

        authorId = postToUpdate.get().getAuthorId();
        if (!userId.equals(authorId)) {
            return helpers.response("You are not authorized to update this post", HttpStatus.UNAUTHORIZED);
        }

        Post updatedPost = postToUpdate.get();
        updatedPost.setTitle(post.getTitle());
        updatedPost.setBody(post.getBody());
        updatedPost.setImage(post.getImage());
        updatedPost.setTags(post.getTags() != null ? post.getTags() : new ArrayList<>());
        updatedPost.setLikes(post.getLikes() != null ? post.getLikes() : new ArrayList<>());
        updatedPost.setReviewIds(post.getReviewIds() != null ? post.getReviewIds() : new ArrayList<>());
        updatedPost.setUpdatedAt(LocalDateTime.now());
        postRepository.save(updatedPost);
        return helpers.responseWithData("Post updated successfully", HttpStatus.OK, updatedPost);
    }

    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable ObjectId postId, @PathVariable String userId) {
        Optional<Post> postToDelete = postRepository.findById(postId);
        String authorId = "";

        if (!postRepository.existsById(postId)) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        if (postToDelete.isPresent()) {
            authorId = postToDelete.get().getAuthorId();
        }
        if (!userId.equals(authorId)) {
            return helpers.response("You are not authorized to delete this post", HttpStatus.UNAUTHORIZED);
        }

        postRepository.deleteById(postId);
        return helpers.response("Post deleted successfully", HttpStatus.OK);
    }
}
