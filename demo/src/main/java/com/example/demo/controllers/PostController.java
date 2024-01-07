package com.example.demo.controllers;

import com.example.demo.models.Post;
import com.example.demo.services.PostService;
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

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }
}
