package com.example.demo.JwtFilter;

import com.example.demo.SecurityConfig.CustomUserDetails;
import com.example.demo.utils.JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final CustomUserDetails customUserDetails;

    @Autowired
    public JwtFilter(JwtHelper jwtHelper, CustomUserDetails customUserDetails) {
        this.jwtHelper = jwtHelper;
        this.customUserDetails = customUserDetails;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        String email = jwtHelper.extractEmail(token);

        UserDetails userDetails = customUserDetails.loadUserByUsername(email);
        if (Boolean.FALSE.equals(jwtHelper.validateToken(token, userDetails))) {
            return;
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails((new WebAuthenticationDetailsSource()));
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);

    }
}
