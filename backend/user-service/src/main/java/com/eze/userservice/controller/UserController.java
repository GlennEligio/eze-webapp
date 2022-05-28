package com.eze.userservice.controller;

import com.eze.userservice.config.EzeUserDetails;
import com.eze.userservice.domain.User;
import com.eze.userservice.dto.LoginUserDto;
import com.eze.userservice.dto.UserDto;
import com.eze.userservice.dto.UserWithTokenDto;
import com.eze.userservice.service.UserService;
import com.eze.userservice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService service;
    private final JwtUtil jwtUtil;

    public UserController(UserService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(service.findAllUsers());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username){
        return ResponseEntity.ok(service.findUser(username));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        user.setDeleteFlag(false);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(user));
    }

    @PutMapping("/users")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody User user) {
        service.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<Object> deleteUser(@PathVariable("username") String username){
        service.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserWithTokenDto> login(@RequestBody LoginUserDto user){
        User authenticatedUser = service.authenticateUser(user.getUsername(), user.getPassword());
        EzeUserDetails userDetails = new EzeUserDetails(authenticatedUser);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new UserWithTokenDto(authenticatedUser.getUsername(), authenticatedUser.getRole(), accessToken, refreshToken));
    }

    @GetMapping("/users/refresh/{refreshToken}")
    public ResponseEntity<UserWithTokenDto> refreshToken(@PathVariable("refreshToken") String refreshToken){
        String username = jwtUtil.extractUsername(refreshToken);
        User authenticatedUser = service.findUser(username);
        EzeUserDetails userDetails = new EzeUserDetails(authenticatedUser);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        return ResponseEntity.ok(new UserWithTokenDto(authenticatedUser.getUsername(), authenticatedUser.getRole(), accessToken, refreshToken));
    }

    @GetMapping("/users/validate/{accessToken}")
    public ResponseEntity<UserDto> validateToken(@PathVariable("accessToken") String accessToken){
        String username = jwtUtil.extractUsername(accessToken);
        User authenticateUser = service.findUser(username);
        return ResponseEntity.ok(new UserDto(authenticateUser.getUsername(), authenticateUser.getRole().name()));
    }
}
