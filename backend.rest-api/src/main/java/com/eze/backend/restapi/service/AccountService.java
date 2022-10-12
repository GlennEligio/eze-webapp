package com.eze.backend.restapi.service;

import com.eze.backend.restapi.dtos.EzeUserDetails;
import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements IService<Account>, UserDetailsService {

    private AccountRepository repository;

    @Override
    public List<Account> getAll() {
        return repository.findAll();
    }

    @Override
    public Account get(Serializable code) {
        return repository.findByAccountCode(code.toString())
                .orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
    }

    @Override
    public Account create(Account account) {
        if (account.getAccountCode() != null) {
            Optional<Account> accOp = repository.findByAccountCode(account.getAccountCode());
            if (accOp.isPresent()) {
                throw new ApiException(alreadyExist(account.getAccountCode()), HttpStatus.BAD_REQUEST);
            }
        }
        account.setAccountCode(new ObjectId().toHexString());
        account.setCreatedAt(LocalDateTime.now());
        return repository.save(account);
    }

    @Override
    public Account update(Account account, Serializable code) {
        Account account1 = repository.findByAccountCode(code.toString())
                .orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        account1.update(account);
        return repository.save(account1);
    }

    @Override
    public void delete(Serializable code) {
        Account account = repository.findByAccountCode(code.toString())
                .orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        repository.delete(account);
    }

    @Override
    public String notFound(Serializable code) {
        return "No account with account code " + code.toString() + " was found";
    }

    @Override
    public String alreadyExist(Serializable code) {
        return "Account with code " + code.toString() + " already exist";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found"));
        return new EzeUserDetails(account);
    }
}
