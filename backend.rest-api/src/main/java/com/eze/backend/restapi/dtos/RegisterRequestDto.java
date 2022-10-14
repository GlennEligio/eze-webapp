package com.eze.backend.restapi.dtos;

import com.eze.backend.restapi.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

// TODO: Add validation constraints to the DTO properties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
    @NotBlank
    private String email;

    public Account createAccount() {
        Account account = new Account();
        account.setUsername(this.username);
        account.setPassword(this.password);
        account.setFullName(this.fullName);
        account.setEmail(this.email);
        return account;
    }
}
