package com.example.demo.controllers;

import com.example.demo.models.Post;
import com.example.demo.services.PostService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/test")
    public String test() {
        return "Hello World";
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
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @PutMapping("/{id}/update/{userId}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable ObjectId id, @PathVariable String userId, @RequestBody Post post) {
        return postService.updatePost(id, userId, post);
    }
}
