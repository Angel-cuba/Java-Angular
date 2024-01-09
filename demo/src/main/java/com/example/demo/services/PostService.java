package com.example.demo.services;

import com.example.demo.models.Post;
import com.example.demo.models.dto.Posts.PostRequest;
import com.example.demo.models.dto.Posts.PostUpdateRequest;
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
        return helpers.responseWithData("Posts retrieved successfully", HttpStatus.OK, postRepository.findAll());
    }

    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable ObjectId id) {
        if (!postRepository.existsById(id)) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        return helpers.responseWithData("Post retrieved successfully", HttpStatus.OK, postRepository.findById(id));
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
        post.setTags(postRequest.getTags());
        post.setLikes(postRequest.getLikes());
        post.setReviewIds(postRequest.getReviewIds());
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return helpers.responseWithData("Post created successfully", HttpStatus.CREATED, savedPost.getId());
    }

    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable ObjectId id, @PathVariable String userId, @RequestBody PostUpdateRequest post) {
        Optional<Post> postToUpdate = postRepository.findById(id);
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
        updatedPost.setTags(post.getTags());
        updatedPost.setUpdatedAt(LocalDateTime.now());
        postRepository.save(updatedPost);
        return helpers.responseWithData("Post updated successfully", HttpStatus.OK, updatedPost);
    }

    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable ObjectId id, @PathVariable String userId) {
        Optional<Post> postToDelete = postRepository.findById(id);
        String authorId = "";

        if (!postRepository.existsById(id)) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        if (postToDelete.isPresent()) {
            authorId = postToDelete.get().getAuthorId();
        }
        if (!userId.equals(authorId)) {
            return helpers.response("You are not authorized to delete this post", HttpStatus.UNAUTHORIZED);
        }

        postRepository.deleteById(id);
        return helpers.response("Post deleted successfully", HttpStatus.OK);
    }
}
