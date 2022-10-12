package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.model.AccountFingerprint;
import com.eze.backend.restapi.repository.AccountFingerprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountFingerprintService implements IService<AccountFingerprint> {

    private AccountFingerprintRepository repository;
    private AccountService accountService;

    @Override
    public List<AccountFingerprint> getAll() {
        return repository.findAll();
    }

    @Override
    public AccountFingerprint get(Serializable accountCode) {
        return repository.findByAccountCode(accountCode.toString()).orElseThrow(() -> new ApiException(notFound(accountCode), HttpStatus.NOT_FOUND));
    }

    @Override
    public AccountFingerprint create(AccountFingerprint acFingerprint) {
        if (acFingerprint.getAccount() != null && acFingerprint.getAccount().getAccountCode() != null) {
            // Check if account with accountCode exist
            Account account = accountService.get(acFingerprint.getAccount().getAccountCode());
            // If it exists, check a fingerprint already exist for the account
            if (account.getAccountFingerprint() != null) {
                // If fingerprint already present, cancel save
                throw new ApiException(alreadyExist(account.getAccountCode()), HttpStatus.BAD_REQUEST);
            }
            // Else save fingerprint alongside the account
            acFingerprint.setAccount(account);
            return repository.save(acFingerprint);
        }
        // cancel fingerprint save, no account attached to the fingerprint
        throw new ApiException("No account is attached to fingerprint", HttpStatus.BAD_REQUEST);
    }

    @Override
    public AccountFingerprint update(AccountFingerprint acFingerprint, Serializable accountCode) {
        // Using accountCode, check if an account exist
        Account account = accountService.get(accountCode);
        // update the fingerprint data in the account
        AccountFingerprint af = account.getAccountFingerprint();
        af.setFingerprint(acFingerprint.getFingerprint());
        return repository.save(af);
    }

    @Override
    public void delete(Serializable accountCode) {
        // Using accountCode, find the account
        Account account = accountService.get(accountCode);
        // Set the fingerprint to null
        AccountFingerprint af = account.getAccountFingerprint();
        repository.delete(af);
    }

    @Override
    public String notFound(Serializable code) {
        return "No account fingerprint with for account code " + code + " was found";
    }

    @Override
    public String alreadyExist(Serializable code) {
        return "AccountFingerprint for account with code " + code + " already exist";
    }
}
