package com.example.demo.models.dto.posts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {
    private String title;
    private String body;
    private String image;
    private List<String> tags;
    private List<String> likes;
    private List<String> reviewIds;
    private LocalDateTime updatedAt;
}
