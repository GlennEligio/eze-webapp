package com.eze.backend.spring.model;

import com.eze.backend.spring.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Enumerated(EnumType.ORDINAL)
    private AccountType type;
    private byte[] profile;
    private LocalDateTime createdAt;
    private Boolean active;
    private Boolean deleteFlag;
//    @OneToOne(mappedBy = "account")
//    private AccountFingerprint accountFingerprint;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(fullName, account.fullName) && Objects.equals(username, account.username) && Objects.equals(email, account.email) && Objects.equals(password, account.password) && type == account.type && Arrays.equals(profile, account.profile) && Objects.equals(createdAt, account.createdAt) && Objects.equals(active, account.active) && Objects.equals(deleteFlag, account.deleteFlag);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, fullName, username, email, password, type, createdAt, active, deleteFlag);
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
        if(newAccount.getDeleteFlag() != null) {
            this.setDeleteFlag(newAccount.getDeleteFlag());
        }
    }
}
