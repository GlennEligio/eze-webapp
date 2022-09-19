package com.eze.backend.restapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    // Will be an enum later on
    private String type;
    private byte[] profile;
    private LocalDateTime createdAt;
}
