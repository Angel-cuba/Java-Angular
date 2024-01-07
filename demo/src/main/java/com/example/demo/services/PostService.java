package com.example.demo.services;

import com.example.demo.models.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.utils.Helpers;
import org.bson.types.ObjectId;
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Helpers helpers;

    private boolean isPostValid(Post post) {
        return post.getTitle() == null || post.getTitle().isEmpty() || post.getBody() == null || post.getBody().isEmpty() || post.getAuthorId() == null || post.getAuthorId().isEmpty() || post.getImage() == null || post.getImage().isEmpty();
    }

    private boolean isPostValidForUpdate(Post post) {
        return post.getTitle() == null || post.getTitle().isEmpty() || post.getBody() == null || post.getBody().isEmpty() || post.getImage() == null || post.getImage().isEmpty();
    }

    public ResponseEntity<Map<String, Object>> getAllPosts() {
        return helpers.responseWithData("Posts retrieved successfully", HttpStatus.OK, postRepository.findAll());
    }

    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable ObjectId id) {
        if(!postRepository.existsById(id)) {
            return helpers.response("Post not found", HttpStatus.NOT_FOUND);
        }
        return helpers.responseWithData("Post retrieved successfully", HttpStatus.OK, postRepository.findById(id));
    }

    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Post post) {
        if (isPostValid(post)) {
            return helpers.response("Invalid post", HttpStatus.BAD_REQUEST);
        }
        post.setTitle(post.getTitle());
        post.setBody(post.getBody());
        post.setAuthorId(post.getAuthorId());
        post.setImage(post.getImage());
        post.setTags(post.getTags());
        post.setLikes(post.getLikes());
        post.setReviewIds(post.getReviewIds());
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
        return helpers.responseWithData("Post created successfully", HttpStatus.CREATED, post.getId());
    }

    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable ObjectId id, @PathVariable String userId, @RequestBody Post post) {
        Optional<Post> postToUpdate = postRepository.findById(id);
        String authorId = "";

        if (!postRepository.existsById(id)) {
            return helpers.response("Post not found", HttpStatus.NOT_FOUND);
        }
        if (isPostValidForUpdate(post)) {
            return helpers.response("Invalid post", HttpStatus.BAD_REQUEST);
        }
        if (postToUpdate.isPresent()) {
            authorId = postToUpdate.get().getAuthorId();
        }
        if(!userId.equals(authorId)) {
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
}
