package com.eze.backend.restapi.dtos;

import com.eze.backend.restapi.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "Username cant be blank")
    private String username;
    @NotBlank(message = "Password can't be blank")
    private String password;
    @NotBlank(message = "Full name can't be blank")
    private String fullName;
    @NotBlank(message = "Email can't be blank")
    private String email;
    @URL(message = "Profile image url must be a valid url",protocol = "http")
    private String profile;

    public Account createAccount() {
        Account account = new Account();
        account.setUsername(this.username);
        account.setPassword(this.password);
        account.setFullName(this.fullName);
        account.setEmail(this.email);
        account.setProfile(this.profile);
        return account;
    }
}
