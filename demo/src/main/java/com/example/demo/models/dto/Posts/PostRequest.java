package com.example.demo.models.dto.Posts;

import com.example.demo.models.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String id;
    private String title;
    private String body;
    private String authorId;
    private String image;
    private List<String> tags;
    private List<String> likes;
    private List<Review> reviewIds;
    private LocalDateTime createdAt;
}
