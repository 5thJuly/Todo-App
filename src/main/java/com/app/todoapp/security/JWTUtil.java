package com.app.todoapp.security;

import com.app.todoapp.entities.Users;

import com.app.todoapp.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JWTUtil {

    private final UserRepository userRepository;
    private final JWTHelper jwtHelper;
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${jwt.secret}")
    private String secretKey;

    public int extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        String email = jwtHelper.getEmailFromToken(token);

        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        if (claims == null) {
            throw new IllegalArgumentException("Invalid token: uid claim is missing or null");
        }

        return user.getUserId();
    }



    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Error extracting claims from token", e);
            throw e;
        }
    }
}