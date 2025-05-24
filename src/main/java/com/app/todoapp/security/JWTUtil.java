package com.app.todoapp.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JWTUtil {

    private final JWTHelper jwtHelper;
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${jwt.secret}")
    private String secretKey;

    public Long extractUserId(String token) {
        if (jwtHelper.isTokenExpired(token)) {
            logger.error("Token is expired: {}", token);
            throw new IllegalArgumentException("Token is expired");
        }
        Long userId = jwtHelper.getUserIdFromToken(token);
        if (userId == null) {
            logger.error("Invalid token: userId claim is missing or null");
            throw new IllegalArgumentException("Invalid token: userId claim is missing");
        }
        return userId;
    }
}