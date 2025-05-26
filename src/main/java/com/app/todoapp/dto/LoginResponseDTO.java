package com.app.todoapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private Integer userId;
    private String userName;
    private String email;
    private String profileImg;
    private boolean success;
    private String message;
    private String token;
}
