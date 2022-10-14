package com.eze.backend.restapi.service;

import com.eze.backend.restapi.dtos.EzeUserDetails;
import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AccountService implements IService<Account>, UserDetailsService {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, @Lazy PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Account> getAll() {
        return repository.findAll();
    }

    @Override
    public Account get(Serializable username) {
        return repository.findByUsername(username.toString())
                .orElseThrow(() -> new ApiException(notFound(username), HttpStatus.NOT_FOUND));
    }

    @Override
    public Account create(Account account) {
        if (account.getUsername() != null) {
            Optional<Account> accOp = repository.findByUsername(account.getUsername());
            if (accOp.isPresent()) {
                throw new ApiException(alreadyExist(account.getUsername()), HttpStatus.BAD_REQUEST);
            }
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setCreatedAt(LocalDateTime.now());
            account.setActive(true);
            return repository.save(account);
        }
        throw new ApiException("No username found in Account to create", HttpStatus.BAD_REQUEST);
    }

    @Override
    public Account update(Account account, Serializable username) {
        Account account1 = repository.findByUsername(username.toString())
                .orElseThrow(() -> new ApiException(notFound(username), HttpStatus.NOT_FOUND));
        return repository.save(account1);
    }

    @Override
    public void delete(Serializable username) {
        Account account = repository.findByUsername(username.toString())
                .orElseThrow(() -> new ApiException(notFound(username), HttpStatus.NOT_FOUND));
        repository.delete(account);
    }

    @Override
    public String notFound(Serializable username) {
        return "No account with account username " + username.toString() + " was found";
    }

    @Override
    public String alreadyExist(Serializable username) {
        return "Account with username " + username.toString() + " already exist";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading account with username {}", username);
        Account account = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No account found with username " + username));
        log.info("Account found: {}", account);
        return new EzeUserDetails(account);
    }
}
