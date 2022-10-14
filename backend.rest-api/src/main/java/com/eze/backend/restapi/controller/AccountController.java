package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.EzeUserDetails;
import com.eze.backend.restapi.dtos.LoginRequestDto;
import com.eze.backend.restapi.dtos.LoginResponseDto;
import com.eze.backend.restapi.dtos.RegisterRequestDto;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.service.AccountService;
import com.eze.backend.restapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class AccountController {

    private AccountService service;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    public AccountController(AccountService service, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/accounts/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("Account with username {} is trying to log in", loginRequestDto.getUsername());
        EzeUserDetails userDetails = (EzeUserDetails) service.loadUserByUsername(loginRequestDto.getUsername());
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect credentials");
        }
        final String accessToken = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        log.info("Authentication success with jwt: " + accessToken);
        return ResponseEntity.ok(new LoginResponseDto(userDetails.getUsername(), accessToken, refreshToken));
    }

    @PostMapping("/accounts/register")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        log.info("Account with username {} is registering", registerRequestDto.getUsername());
        Account account = registerRequestDto.createAccount();
        Account accountSaved = service.create(account);
        log.info("Account with username {} is registered", accountSaved.getUsername());
        EzeUserDetails userDetails = (EzeUserDetails) service.loadUserByUsername(accountSaved.getUsername());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        log.info("Generated access token {}, and refresh token {}", accessToken, refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(userDetails.getUsername(), accessToken, refreshToken));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/accounts/{username}")
    public ResponseEntity<Account> getAccount(@PathVariable("username") String username) {
        return ResponseEntity.ok(service.get(username));
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(account));
    }

    @PutMapping("/accounts/{username}")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account,
                                                 @PathVariable("username") String username) {
        return ResponseEntity.ok(service.update(account, username));
    }

    @DeleteMapping("/accounts/{username}")
    public ResponseEntity<Object> deleteAccount(@PathVariable("username") String username) {
        service.delete(username);
        return ResponseEntity.ok().build();
    }


}
