package com.example.demo.models.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String id;
    private String username;
    private String password;
    private String email;
    private String image;
    private String linkedin;
    private String github;
    private String bio;
    private String role;
}
