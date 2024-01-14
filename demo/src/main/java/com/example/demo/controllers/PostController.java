package com.example.demo.controllers;

import com.example.demo.models.dto.posts.PostRequest;
import com.example.demo.models.dto.posts.PostUpdateRequest;
import com.example.demo.services.PostService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {


    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable ObjectId id) {
        return postService.getPostById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody PostRequest post) {
        return postService.createPost(post);
    }

    @PutMapping("/{id}/update/{userId}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable ObjectId id, @PathVariable String userId, @RequestBody PostUpdateRequest post) {
        return postService.updatePost(id, userId, post);
    }

    @DeleteMapping("/{id}/delete/{userId}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable ObjectId id, @PathVariable String userId) {
        return postService.deletePost(id, userId);
    }
}
