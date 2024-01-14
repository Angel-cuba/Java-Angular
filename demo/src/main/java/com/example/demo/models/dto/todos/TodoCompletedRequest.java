package com.example.demo.models.dto.todos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoCompletedRequest {
    private Boolean completed;
}
