package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.EzeUserDetails;
import com.eze.backend.restapi.dtos.LoginRequestDto;
import com.eze.backend.restapi.dtos.LoginResponseDto;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class AccountController {

    private AccountService service;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    @PostMapping("/accounts/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        EzeUserDetails userDetails = (EzeUserDetails) service.loadUserByUsername(loginRequestDto.getUsername());
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect credentials");
        }
        final String accessToken = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        log.info("Authentication success with jwt: " + accessToken);
        return ResponseEntity.ok(new LoginResponseDto(userDetails.getUsername(), userDetails.getAccountCode(), accessToken, refreshToken));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/accounts/{code}")
    public ResponseEntity<Account> getAccount(@PathVariable("code") String code) {
        return ResponseEntity.ok(service.get(code));
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(account));
    }

    @PutMapping("/accounts/{code}")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account,
                                                 @PathVariable("code") String code) {
        return ResponseEntity.ok(service.update(account, code));
    }

    @DeleteMapping("/accounts/{code}")
    public ResponseEntity<Object> deleteAccount(@PathVariable("code") String code) {
        service.delete(code);
        return ResponseEntity.ok().build();
    }


}
