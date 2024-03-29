package com.example.demo.models.dto.reviews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private String body;
    private String authorId;
}
