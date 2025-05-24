package com.app.todoapp.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTHelper {
    private static final Logger logger = LoggerFactory.getLogger(JWTHelper.class);
    public static final long JWT_TOKEN_VALIDITY = 60 * 60 * 24;

    @Value("${jwt.secret}")
    private String secret;

    public String getEmailFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (JwtException e) {
            logger.error("Error extracting email from token: {}", token, e);
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            return getClaimFromToken(token, claims -> claims.get("userId", Long.class));
        } catch (JwtException e) {
            logger.error("Error extracting userId from token: {}", token, e);
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getExpiration);
        } catch (JwtException e) {
            logger.error("Error extracting expiration date from token: {}", token, e);
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            logger.error("Error parsing JWT token: {}", token, e);
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            logger.error("Error checking token expiration: {}", token, e);
            return true; // Coi như expired nếu có lỗi
        }
    }

    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        claims.put("userId", userId); // Thêm userId vào claims
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = getEmailFromToken(token);
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            logger.error("Error validating token: {}", token, e);
            return false;
        }
    }
}