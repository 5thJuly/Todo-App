package com.app.todoapp.config;



import com.app.todoapp.security.JWTAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless API
                .addFilterBefore(corsFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class) // Add CORS filter
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints for registration and other TodoApp routes
                        .requestMatchers("/*/*/register", "/api/*/login","/api/*/google", "api/*/forgot-password", "api/*/reset-password",
                                "/verify-email", "/resend-verification-email").permitAll()
                        // Public TodoApp endpoints (e.g., view public todos or categories)
                        .requestMatchers("/todos/public", "/todos/categories").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}