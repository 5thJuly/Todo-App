package com.app.todoapp.security;

import com.app.todoapp.dto.UserLogin;
import com.app.todoapp.entities.Users; // Ensure this import matches your User entity
import com.app.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JWTHelper jwtHelper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); // Replace with actual roles if needed

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(authority))
                .build();
    }

    public String loginToken(UserLogin request) {
        try {
            UserDetails userDetails = this.loadUserByUsername(request.getEmail());
            return jwtHelper.generateToken(userDetails);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }
}