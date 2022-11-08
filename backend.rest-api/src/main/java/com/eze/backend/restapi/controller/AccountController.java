package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.EzeUserDetails;
import com.eze.backend.restapi.dtos.LoginRequestDto;
import com.eze.backend.restapi.dtos.LoginResponseDto;
import com.eze.backend.restapi.dtos.RegisterRequestDto;
import com.eze.backend.restapi.enums.AccountType;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.service.AccountService;
import com.eze.backend.restapi.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("Account with username {} is trying to log in", loginRequestDto.getUsername());
        EzeUserDetails userDetails = (EzeUserDetails) service.loadUserByUsername(loginRequestDto.getUsername());
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect credentials");
        }
        final String accessToken = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        log.info("Authentication success with jwt: " + accessToken);
        return ResponseEntity.ok(new LoginResponseDto(userDetails.getUsername(), userDetails.getAuthorities().stream().findFirst().get().toString(), userDetails.getFullName(), accessToken, refreshToken));
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
        return ResponseEntity.ok(new LoginResponseDto(userDetails.getUsername(), userDetails.getAuthorities().stream().findFirst().get().toString(), userDetails.getFullName(), accessToken, refreshToken));
    }

    @PostMapping("/accounts/upload")
    public ResponseEntity<Object> upload(@RequestParam(required = false, defaultValue = "false") Boolean overwrite,
                                         @RequestParam MultipartFile file) {
        log.info("Preparing Excel for Item Database update");
        if(!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            throw new ApiException("Can only upload .xlsx files", HttpStatus.BAD_REQUEST);
        }
        List<Account> accounts = service.excelToList(file);
        int itemsAffected = service.addOrUpdate(accounts, overwrite);
        log.info("Updated {} item database using the excel file", itemsAffected);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("Items Affected", itemsAffected);
        return ResponseEntity.ok(objectNode);
    }

    @GetMapping("/accounts/download")
    public void download(HttpServletResponse response) throws IOException {
        log.info("Preparing Item list for Download");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=accounts.xlsx");
        ByteArrayInputStream stream = service.listToExcel(service.getAll().stream().filter(a -> !a.getType().equals(AccountType.SADMIN)).toList());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok(service.getAllNotDeleted().stream().filter(account -> account.getType() != AccountType.SADMIN).toList());
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
        service.softDelete(username);
        return ResponseEntity.ok().build();
    }


}
