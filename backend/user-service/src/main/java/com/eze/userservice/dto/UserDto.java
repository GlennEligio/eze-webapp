package com.eze.userservice.dto;

import com.eze.userservice.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Boolean deleteFlag;
    private Role role;
}
