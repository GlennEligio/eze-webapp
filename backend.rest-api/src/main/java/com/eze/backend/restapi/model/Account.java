package com.eze.backend.restapi.model;

import com.eze.backend.restapi.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, name = "account_code")
    private String accountCode;
    private String fullName;
    @Column(unique = true, nullable = false)
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private AccountType type;
    private byte[] profile;
    private LocalDateTime createdAt;
    private Boolean active;
    @OneToOne(mappedBy = "account")
    private AccountFingerprint accountFingerprint;

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
    }
}
