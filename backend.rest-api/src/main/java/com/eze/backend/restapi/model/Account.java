package com.eze.backend.restapi.model;

import com.eze.backend.restapi.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
//    @OneToOne(mappedBy = "account")
//    private AccountFingerprint accountFingerprint;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(fullName, account.fullName) && Objects.equals(username, account.username) && Objects.equals(email, account.email) && type == account.type && Arrays.equals(profile, account.profile) && Objects.equals(createdAt, account.createdAt) && Objects.equals(active, account.active);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, fullName, username, email, password, type, createdAt, active);
        result = 31 * result + Arrays.hashCode(profile);
        return result;
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
    }
}
