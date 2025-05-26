package com.app.todoapp.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.app.todoapp.dto.GoogleTokenDTO;
import com.app.todoapp.dto.GoogleUserInfoDTO;
import com.app.todoapp.dto.LoginResponseDTO;
import com.app.todoapp.dto.PasswordResetDTO;
import com.app.todoapp.dto.RegistrationDTO;
import com.app.todoapp.dto.UpdateProfileDTO;
import com.app.todoapp.entities.Users;
import com.app.todoapp.exception.ResourceNotFoundException;
import com.app.todoapp.mail.EmailService;
import com.app.todoapp.repository.UserRepository;
import com.app.todoapp.service.interfaces.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private Map<String, String> resetTokens = new HashMap<>();

    private static final String GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";

    @Override
    public LoginResponseDTO register(RegistrationDTO registrationDTO) {
        try {
            if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
                throw new IllegalArgumentException("Password and Confirm Password không khớp nhau");
            }
            if (userRepository.existsByUsername(registrationDTO.getUsername())) {
                throw new RuntimeException("Username đã tồn tại", new IllegalArgumentException("Username: " + registrationDTO.getUsername()));
            }

            
            if (userRepository.existsByEmail(registrationDTO.getEmail())) {
                throw new RuntimeException("Email đã tồn tại", new IllegalArgumentException("Email: " + registrationDTO.getEmail()));
            }

            
            Users user = Users.builder()
                    .username(registrationDTO.getUsername())
                    .email(registrationDTO.getEmail())
                    .password(passwordEncoder.encode(registrationDTO.getPassword()))
                    .profileImg("https://cdn-icons-png.flaticon.com/512/8801/8801434.png")
                    .build();

            Users savedUser = userRepository.save(user);
            return new LoginResponseDTO(savedUser.getUserId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getProfileImg(), true, "Registration successful", null);
    
            
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }
    

    @Override
    public LoginResponseDTO login(String email, String password) throws Exception {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User not found with email: " + email);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Password không đúng");
        }

        return new LoginResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImg(),
                true,
                "Login thành công!!",
                null
        );
    }
    @Override
    public LoginResponseDTO authenticateWithGoogle(GoogleTokenDTO tokenDTO) {
        // Verify Google token
        GoogleUserInfoDTO googleUserInfoDTO = verifyGoogleToken(tokenDTO.getAccessToken());

        if (googleUserInfoDTO == null || !googleUserInfoDTO.isEmail_verified()) {
            throw new IllegalArgumentException("Invalid Google token or email not verified");
        }

        // Find existing user
        Users existingUser = userRepository.findByEmail(googleUserInfoDTO.getEmail());
        
        Users user;
        String message;

        if (existingUser != null) {
            existingUser.setLastLogin(LocalDateTime.now());
            user = existingUser;
            message = "Login thành công";
        } else {
            // Create new user
            user = createNewUserFromGoogle(googleUserInfoDTO);
            message = "Registration thành công. Vui lòng kiểm tra email để xác thực tài khoản!";
        }
        // Save user
        user = userRepository.save(user);

        return new LoginResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImg(),
                true,
                message,
                null
        );
    }

    @Override
    public GoogleUserInfoDTO verifyGoogleToken(String accessToken) {
        try {
            String url = GOOGLE_TOKEN_INFO_URL + accessToken;
            GoogleUserInfoDTO userInfo = restTemplate.getForObject(url, GoogleUserInfoDTO.class);

            if (userInfo == null || userInfo.getEmail() == null) {
                throw new RuntimeException("Failed to verify Google token");
            }

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Error verifying Google token: " + e.getMessage());
        }
    }

    private Users createNewUserFromGoogle(GoogleUserInfoDTO googleUser) {
        try {
            Users user = Users.builder()
                    .email(googleUser.getEmail())
                    .username(generateUsername(googleUser.getEmail()))
                    .password(passwordEncoder.encode("GOOGLE_AUTH_USER"))
                    .profileImg(googleUser.getPicture())
                    .uid(googleUser.getSub())
                    .createdAt(LocalDateTime.now())
                    .build();


            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user from Google data", e);
        }
    }

    private String generateUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        return username;
    }


    @Override
    public void updateProfile(Integer userId, UpdateProfileDTO dto) {
        Users user = userRepository.findByUserId(userId);
        if(user == null) {
            throw new RuntimeException("User not found");
        }
        if(user.getUsername() != null) {
            user.setUsername(dto.getUserName());
        }
        if(user.getProfileImg() != null) {
            user.setProfileImg(dto.getProfileImg());
        }
    
        userRepository.save(user);
    }


    @Override
    public void updatePassword(Integer userId, UpdateProfileDTO dto) {
        if (userId == null || dto == null || dto.getPassword() == null || dto.getConfirmPassword() == null) {
            throw new IllegalArgumentException("User ID, Password, or Confirm Password không được để trống");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password không khớp nhau");
        }
        Users user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }


    public String forgotPassword(String email) {
        try {
            Optional<Users> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            
            if (userOptional.isEmpty()) {
                throw new RuntimeException("Email không tồn tại trong hệ thống");
            }
            
            Users user = userOptional.get();
            
            String resetToken = generateResetToken();
            
            String tokenKey = email + "_" + System.currentTimeMillis();
            resetTokens.put(tokenKey, resetToken);
            
            String subject = "Đặt lại mật khẩu - TodoApp";
            String emailContent = emailService.buildResetPasswordEmail(user.getUsername(), resetToken);
            
            emailService.sendSimpleEmail(email, subject, emailContent);
            
            scheduleTokenCleanup(tokenKey, 15 * 60 * 1000); // 15 minutes
            
            return "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư!";
            
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email đặt lại mật khẩu: " + e.getMessage());
        }
}
    public String resetPassword(String email, String token, PasswordResetDTO passwordResetDTO) {
    try {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token không được để trống");
        }
        
        if (!passwordResetDTO.getPassword().equals(passwordResetDTO.getConfirm())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }
        
        Optional<Users> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Email không tồn tại trong hệ thống");
        }
        
        if (!verifyResetToken(email, token)) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn");
        }
        
        Users user = userOptional.get();
        
        if (passwordEncoder.matches(passwordResetDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu cũ");
        }
        
        user.setPassword(passwordEncoder.encode(passwordResetDTO.getPassword()));
        user.setLastLogin(LocalDateTime.now()); 
        
        userRepository.save(user);
        
        removeUsedToken(email, token);
        
        return "Đặt lại mật khẩu thành công!";
        
    } catch (IllegalArgumentException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException("Không thể đặt lại mật khẩu: " + e.getMessage());
    }
}

    private String generateResetToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000); // 6 digits
        return String.valueOf(token);
    }

    private boolean verifyResetToken(String email, String token) {
        for (Map.Entry<String, String> entry : resetTokens.entrySet()) {
            String key = entry.getKey();
            String storedToken = entry.getValue();
            
            if (key.startsWith(email + "_") && storedToken.equals(token)) {
                // Kiểm tra thời gian expire (15 phút)
                String[] parts = key.split("_");
                if (parts.length >= 2) {
                    long timestamp = Long.parseLong(parts[parts.length - 1]);
                    long currentTime = System.currentTimeMillis();
                    long timeDiff = currentTime - timestamp;
                    
                    // Token còn hạn (15 phút = 900000 ms)
                    return timeDiff <= 15 * 60 * 1000;
                }
            }
        }
        return false;
    }

    private void removeUsedToken(String email, String token) {
        resetTokens.entrySet().removeIf(entry -> 
            entry.getKey().startsWith(email + "_") && entry.getValue().equals(token));
    }

    private void scheduleTokenCleanup(String tokenKey, long delayMillis) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            resetTokens.remove(tokenKey);
        }
    }, delayMillis);
}
        
   
}
