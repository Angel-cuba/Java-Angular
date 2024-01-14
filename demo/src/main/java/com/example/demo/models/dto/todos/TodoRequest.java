package com.example.demo.models.dto.todos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoRequest {
    private String title;
    private String body;
    private String endDate;
    private Boolean completed;
    private String createdAt;
}
