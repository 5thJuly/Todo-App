package com.app.todoapp.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUserInfoDTO {
    private String email;
    private String name;
    private String picture;
    private String sub;
    private boolean email_verified;
}
