package com.app.todoapp.api;

import com.app.todoapp.dto.*;
import com.app.todoapp.response.ApiResponse;
import com.app.todoapp.security.JWTHelper;
import com.app.todoapp.security.JWTUtil;
import com.app.todoapp.security.MyUserDetailService;
import com.app.todoapp.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final MyUserDetailService userDetailService;
    private final JWTUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegistrationDTO registrationDTO,
                                                HttpServletRequest request) {
        try {
            logger.info("Registration attempt for email: {}", registrationDTO.getEmail());

            LoginResponseDTO response = userService.register(registrationDTO);

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message("Đăng ký thành công!")
                    .data(response)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            logger.info("Registration successful for email: {}", registrationDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

        } catch (IllegalArgumentException e) {
            logger.error("Registration failed - validation error: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);

        } catch (RuntimeException e) {
            logger.error("Registration failed: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserLogin loginRequest,
                                             HttpServletRequest request) {
        try {
            logger.info("Login attempt for email: {}", loginRequest.getEmail());

            LoginResponseDTO loginResponse = userService.login(loginRequest.getEmail(), loginRequest.getPassword());

            // Generate JWT token
            String token = userDetailService.generateToken(loginRequest.getEmail());

            // Create response with token
            LoginResponseDTO responseWithToken = LoginResponseDTO.builder()
                    .userId(loginResponse.getUserId())
                    .userName(loginResponse.getUserName())
                    .email(loginResponse.getEmail())
                    .profileImg(loginResponse.getProfileImg())
                    .success(true)
                    .message(loginResponse.getMessage())
                    .token(token)
                    .build();

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message("Đăng nhập thành công!")
                    .data(responseWithToken)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            logger.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Login failed for email: {}: {}", loginRequest.getEmail(), e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse> authenticateWithGoogle(@Valid @RequestBody GoogleTokenDTO tokenDTO,
                                                              HttpServletRequest request) {
        try {
            logger.info("Google authentication attempt");

            LoginResponseDTO loginResponse = userService.authenticateWithGoogle(tokenDTO);

            // Generate JWT token
            String token = userDetailService.generateToken(loginResponse.getEmail());

            // Create response with token
            LoginResponseDTO responseWithToken = LoginResponseDTO.builder()
                    .userId(loginResponse.getUserId())
                    .userName(loginResponse.getUserName())
                    .email(loginResponse.getEmail())
                    .profileImg(loginResponse.getProfileImg())
                    .success(true)
                    .message(loginResponse.getMessage())
                    .token(token)
                    .build();

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message("Google authentication thành công!")
                    .data(responseWithToken)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            logger.info("Google authentication successful");
            return ResponseEntity.ok(apiResponse);

        } catch (IllegalArgumentException e) {
            logger.error("Google authentication failed - validation error: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);

        } catch (RuntimeException e) {
            logger.error("Google authentication failed: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message("Google authentication thất bại")
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }
    }
    @PatchMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestHeader("Authorization") String authHeader,
                                                     @Valid @RequestBody UpdateProfileDTO updateProfileDTO,
                                                     HttpServletRequest request) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtUtil.extractUserId(token);

            logger.info("Profile update attempt for userId: {}", userId);

            userService.updateProfile(userId.intValue(), updateProfileDTO);

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message("Cập nhật profile thành công!")
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            logger.info("Profile updated successfully for userId: {}", userId);
            return ResponseEntity.ok(apiResponse);

        } catch (IllegalArgumentException e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);

        } catch (RuntimeException e) {
            logger.error("Profile update failed: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestHeader("Authorization") String authHeader,
                                                      @Valid @RequestBody UpdateProfileDTO updateProfileDTO,
                                                      HttpServletRequest request) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtUtil.extractUserId(token);

            logger.info("Password update attempt for userId: {}", userId);

            userService.updatePassword(userId.intValue(), updateProfileDTO);

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message("Cập nhật mật khẩu thành công!")
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            logger.info("Password updated successfully for userId: {}", userId);
            return ResponseEntity.ok(apiResponse);

        } catch (IllegalArgumentException e) {
            logger.error("Password update failed - validation error: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);

        } catch (RuntimeException e) {
            logger.error("Password update failed: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO,
                                                      HttpServletRequest request) {
        try {
            logger.info("Forgot password request for email: {}", forgotPasswordDTO.getEmail());

            String message = userService.forgotPassword(forgotPasswordDTO.getEmail());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message(message)
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            logger.info("Forgot password email sent for: {}", forgotPasswordDTO.getEmail());
            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            logger.error("Forgot password failed for email: {}: {}", forgotPasswordDTO.getEmail(), e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String email,
                                                     @RequestParam String token,
                                                     @Valid @RequestBody PasswordResetDTO passwordResetDTO,
                                                     HttpServletRequest request) {
        try {
            logger.info("Reset password attempt for email: {}", email);

            String message = userService.resetPassword(email, token, passwordResetDTO);

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message(message)
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            logger.info("Password reset successful for email: {}", email);
            return ResponseEntity.ok(apiResponse);

        } catch (IllegalArgumentException e) {
            logger.error("Reset password failed - validation error: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);

        } catch (RuntimeException e) {
            logger.error("Reset password failed: {}", e.getMessage());

            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }


    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        return authHeader.substring(7);
    }

}
