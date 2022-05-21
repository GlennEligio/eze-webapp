package com.eze.userservice.dto;

import com.eze.userservice.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithTokenDto {
    private String username;
    private Role role;
    private String accessToken;
    private String refreshToken;
}
