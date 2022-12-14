package com.eze.backend.spring.dtos;

import com.eze.backend.spring.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUpdateAccountDto {
    @NotBlank(message = "Username cant be blank")
    private String username;
    @NotBlank(message = "Password can't be blank")
    private String password;
    @NotBlank(message = "Full name can't be blank")
    private String fullName;
    @NotBlank(message = "Email can't be blank")
    private String email;
    @URL(message = "Profile image url must be a valid url", regexp = "^(http|https)://.*")
    private String profile;
}
