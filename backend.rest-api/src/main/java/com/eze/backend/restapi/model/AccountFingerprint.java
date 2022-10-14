package com.eze.backend.restapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountFingerprint implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Byte[] fingerprint;
    @OneToOne
    @JoinColumn(name = "account_username", referencedColumnName = "username")
    private Account account;

    public void update(AccountFingerprint ufNew) {
        if(ufNew.getFingerprint() != null) {
            this.fingerprint = ufNew.getFingerprint();
        }
        if(ufNew.getAccount() != null) {
            this.account = ufNew.getAccount();
        }
    }
}
