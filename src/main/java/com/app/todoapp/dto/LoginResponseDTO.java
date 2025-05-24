package com.app.todoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Integer userId;
    private String userName;
    private String email;
    private String profileImg;
    private boolean success;
    private String message;
}
