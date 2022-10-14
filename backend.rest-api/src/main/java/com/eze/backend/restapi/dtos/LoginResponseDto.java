package com.eze.backend.restapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String username;
    private String accountType;
    private String fullName;
    private String accessToken;
    private String refreshToken;
}
