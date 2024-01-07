package com.example.demo.services;

import com.example.demo.models.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.utils.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Helpers helpers;

    private boolean isPostValid(Post post) {
        return post.getTitle() != null && !post.getTitle().isEmpty() && post.getBody() != null && !post.getBody().isEmpty() && post.getAuthorId() != null && !post.getAuthorId().isEmpty() && post.getImage() != null && !post.getImage().isEmpty();
    }

    public ResponseEntity<Map<String, Object>> getAllPosts() {
        return helpers.responseWithData("Posts retrieved successfully", HttpStatus.OK, postRepository.findAll());
    }

    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Post post) {
        if (!isPostValid(post)) {
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
}
