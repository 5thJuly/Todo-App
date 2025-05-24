package com.app.todoapp.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("technihongo.work@gmail.com");
        mailSender.send(message);
    }

    public String buildResetPasswordEmail(String username, String token) {
        return String.format(
            """
            Xin chào %s,
            
            Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Todo_App của mình.
            
            Mã xác nhận của bạn là: %s
            
            Mã này sẽ hết hạn sau 15 phút.
            
            Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
            
            Trân trọng,
            TodoApp Team
            """, 
            username, token
        );
    }
}
