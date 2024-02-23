package com.example.demo.utils;

import com.example.demo.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtHelper {

    private final String secretKey = "THIGH4HBWBY2Y24Y42Y24BONY424YB2YB2Y24YB24Y24YB24";

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        return jwtToken(claims, user.getEmail());
    }

    private String jwtToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws RuntimeException {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            // Manejo de errores
            e.printStackTrace();
            throw new RuntimeException("Error al extraer reclamación del token");
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            // Manejo de errores
            e.printStackTrace();
            throw new RuntimeException("Error al extraer todas las reclamaciones del token");
        }
    }

    private Boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate != null && expirationDate.before(new Date());
    }

    public Object validateToken(String token, UserDetails userDetails) {
        try {
            final String userEmail = extractEmail(token);
            if (!userEmail.equals(userDetails.getUsername()) || isTokenExpired(token)) {
                throw new RuntimeException("Token no válido");
            }
        } catch (Exception e) {
            // Manejo de errores
            e.printStackTrace();
            throw new RuntimeException("Error al validar el token");
        }
        return null;
    }
}
