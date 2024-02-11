package com.example.demo.SecurityConfig;

import com.example.demo.models.dto.user.UserLoginRequest;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetails {

    @Autowired
    private UserRepository userRepository;

    public UserLoginRequest loadUserByUserEmail(String email) throws UsernameNotFoundException {
        Optional<com.example.demo.models.User> user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserLoginRequest(user.get().getEmail(), user.get().getPassword());

    }
}
