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
        http = http
                .cors()
                .and()
                .csrf()
                .disable();
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        http
                .authorizeHttpRequests().requestMatchers("/api/v1/users/create", "/api/v1/users/signin")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/products/update/stock").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/addresses").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/payments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/wishes").hasRole("ADMIN")
                .anyRequest()
                .authenticated();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
