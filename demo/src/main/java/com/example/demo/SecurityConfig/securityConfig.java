package com.example.demo.SecurityConfig;

import com.example.demo.JwtFilter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration public class securityConfig {

    private final JwtFilter jwtFilter;

    @Autowired
    public securityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    String admin = "ADMIN";
    String user = "USER";

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
         http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers(HttpMethod.POST, "/api/v1/users/signin", "/api/v1/users/create").permitAll();
                            //Users
                    requests.requestMatchers(HttpMethod.GET, "/api/v1/users/all").hasRole(admin);
                    requests.requestMatchers(HttpMethod.GET, "/api/v1/users/user/{userId}").hasAnyRole(admin, user);
                    requests.requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}/update").hasAnyRole(admin, user);
                    requests.requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}/delete").hasAnyRole(admin, user);
                            //Posts
                    requests.requestMatchers(HttpMethod.GET, "/api/v1/posts/all", "/api/v1/posts/{postId}", "/api/v1/posts/author/{authorId}").permitAll();
                    requests.requestMatchers(HttpMethod.POST, "/api/v1/posts/create", "/api/v1/posts/{postId}/like/{userId}").hasAnyRole(admin, user);
                    requests.requestMatchers(HttpMethod.POST, "/api/v1/{postId}/like/{userId}").hasAnyRole(admin, user);
                    requests.requestMatchers(HttpMethod.PUT, "/api/v1/posts/{postId}/update/{userId}").hasAnyRole(admin, user);
                    requests.requestMatchers(HttpMethod.DELETE, "/api/v1/posts/{postId}/delete/{userId}").hasAnyRole(admin, user);
                            //Reviews
                    requests.requestMatchers(HttpMethod.GET, "/api/v1/reviews/all/{postId}").permitAll();
                    requests.requestMatchers(HttpMethod.GET, "/api/v1/reviews/review/{reviewId}").hasAnyRole(admin, user);
                    requests.requestMatchers(HttpMethod.POST, "/api/v1/reviews/create/review/{postId}").hasAnyRole(admin, user);
                    requests.requestMatchers(HttpMethod.PUT, "/api/v1/reviews/{postId}/update/{reviewId}/{userId}").hasAnyRole(admin, user);
                    requests.requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/{postId}/delete/{reviewId}/{userId}").hasAnyRole(admin, user);
                    requests.anyRequest().authenticated();
                        }
                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}