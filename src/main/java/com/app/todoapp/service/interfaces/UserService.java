package com.app.todoapp.service.interfaces;

import com.app.todoapp.dto.GoogleTokenDTO;
import com.app.todoapp.dto.GoogleUserInfoDTO;
import com.app.todoapp.dto.LoginResponseDTO;
import com.app.todoapp.dto.PasswordResetDTO;
import com.app.todoapp.dto.RegistrationDTO;
import com.app.todoapp.dto.UpdateProfileDTO;

public interface UserService {
    LoginResponseDTO register(RegistrationDTO registrationDTO);
    LoginResponseDTO login(String email, String password) throws Exception;
    LoginResponseDTO authenticateWithGoogle(GoogleTokenDTO tokenDTO);
    GoogleUserInfoDTO verifyGoogleToken(String accessToken);

    void updateProfile(Integer userId, UpdateProfileDTO dto);
    void updatePassword(Integer userId, UpdateProfileDTO dto);

    String forgotPassword(String email);
    String resetPassword(String email, String token, PasswordResetDTO passwordResetDTO);
    
}
