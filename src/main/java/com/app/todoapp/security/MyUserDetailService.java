package com.app.todoapp.security;

import com.app.todoapp.entities.Users;
import com.app.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailService.class);
    private final UserRepository userRepository;
    private final JWTHelper jwtHelper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        logger.info("Loaded user: {}", email);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // No roles needed
        );
    }

    public String generateToken(String email) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return jwtHelper.generateToken(loadUserByUsername(email), Long.valueOf(user.getUserId()));
    }
}