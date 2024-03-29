package com.eze.backend.spring.model;

import com.eze.backend.spring.dtos.CreateUpdateAccountDto;
import com.eze.backend.spring.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Email can't be blank")
    private String fullName;
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username cant be blank")
    private String username;
    @NotBlank(message = "Email can't be blank")
    private String email;
    @NotBlank(message = "Password can't be blank")
    private String password;
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @URL(message = "Profile image url must be a valid url", regexp = "^(http|https)://.*")
    private String profile;
    private LocalDateTime createdAt;
    private Boolean active;
    private Boolean deleteFlag;
//    @OneToOne(mappedBy = "account")
//    private AccountFingerprint accountFingerprint;


    public Account(String fullName, String username, String email, String password, AccountType type, String profile, LocalDateTime createdAt, Boolean active, Boolean deleteFlag) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.type = type;
        this.profile = profile;
        this.createdAt = createdAt;
        this.active = active;
        this.deleteFlag = deleteFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(fullName, account.fullName) && Objects.equals(username, account.username) && Objects.equals(email, account.email) && Objects.equals(password, account.password) && type == account.type && Objects.equals(profile, account.profile) && Objects.equals(createdAt, account.createdAt) && Objects.equals(active, account.active) && Objects.equals(deleteFlag, account.deleteFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, username, email, password, type, profile, createdAt, active, deleteFlag);
    }

    public void update(Account newAccount) {
        if(newAccount.getEmail() != null) {
            this.setEmail(newAccount.getEmail());
        }
        if(newAccount.getFullName() != null) {
            this.setFullName(newAccount.getFullName());
        }
        if(newAccount.getPassword() != null) {
            this.setPassword(newAccount.getPassword());
        }
        if(newAccount.getType() != null) {
            this.setType(newAccount.getType());
        }
        if(newAccount.getProfile() != null) {
            this.setProfile(newAccount.getProfile());
        }
        if(newAccount.getUsername() != null) {
            this.setUsername(newAccount.getUsername());
        }
        if(newAccount.getActive() != null) {
            this.setActive(newAccount.getActive());
        }
        if(newAccount.getDeleteFlag() != null) {
            this.setDeleteFlag(newAccount.getDeleteFlag());
        }
    }

    public static Account toAccount(CreateUpdateAccountDto dto) {
        Account account = new Account();
        account.setUsername(dto.getUsername());
        account.setPassword(dto.getPassword());
        account.setFullName(dto.getFullName());
        account.setEmail(dto.getEmail());
        account.setProfile(dto.getProfile());
        return account;
    }
}
