package com.app.todoapp.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {
    private String email;
    private String password;
}
