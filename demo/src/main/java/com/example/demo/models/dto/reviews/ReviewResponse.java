package com.example.demo.models.dto.reviews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private String id;
    private String body;
    private String authorId;
    private String createdAt;
    private String updatedAt;

}
