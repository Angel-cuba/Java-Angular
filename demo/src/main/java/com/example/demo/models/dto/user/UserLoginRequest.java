package com.example.demo.models.dto.user;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    private String email;
    private String password;
}
