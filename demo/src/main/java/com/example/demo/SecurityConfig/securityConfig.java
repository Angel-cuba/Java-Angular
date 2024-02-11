package com.example.demo.SecurityConfig;

import com.example.demo.JwtFilter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class securityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] allowedUrls = new String[]{
                //Users
                "/api/v1/users/create",
                "/api/v1/users/signing",
                "/api/v1/users/all",
                "/api/v1/users/user/{userId}",
                "/api/v1/users/{id}/update",
                "/api/v1/users/{id}/delete",
                //Reviews
                "/api/v1/reviews/all/{postId}",
                "/api/v1/reviews/review/{reviewId}",
                "/api/v1/reviews/create/{id}",
                "/api/v1/reviews/{postId}/update/{reviewId}/{userId}",
                "/api/v1/reviews/{postId}/delete/{reviewId}/{userId}",
                //Posts
                "/api/v1/posts/all",
                "/api/v1/posts/{id}",
                "/api/v1/posts/author/{authorId}",
                "/api/v1/posts/create",
                "/api/v1/posts/{postId}/like/{userId}",
                "/api/v1/posts/{id}/update/{userId}",
                "/api/v1/posts/{id}/delete/{userId}"
        };
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests().requestMatchers("/api/v1/users/create", "/api/v1/users/signin").permitAll()
                .requestMatchers(HttpMethod.POST, allowedUrls).permitAll()
                .requestMatchers(HttpMethod.GET, allowedUrls).permitAll()
                .requestMatchers(HttpMethod.PUT, allowedUrls).permitAll()
                .requestMatchers(HttpMethod.DELETE, allowedUrls).permitAll()
                .anyRequest().authenticated();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();


    }
}
