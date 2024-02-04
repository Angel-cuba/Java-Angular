package com.example.demo.models.dto.user;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String password;
    private String email;
    private String image;
    private String linkedin;
    private String github;
    private String bio;

}
