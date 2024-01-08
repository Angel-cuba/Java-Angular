package com.example.demo.models.dto.Todos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoCompletedRequest {
    private Boolean completed;
}
