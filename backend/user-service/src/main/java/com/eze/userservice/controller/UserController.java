package com.eze.userservice.controller;

import com.eze.userservice.config.EzeUserDetails;
import com.eze.userservice.domain.Role;
import com.eze.userservice.domain.User;
import com.eze.userservice.dto.LoginUserDto;
import com.eze.userservice.dto.UserDto;
import com.eze.userservice.dto.UserWithRole;
import com.eze.userservice.dto.UserWithTokenDto;
import com.eze.userservice.exception.ApiException;
import com.eze.userservice.service.UserService;
import com.eze.userservice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // TODO: Add Unit test
    @PostMapping("/users/register")
    public ResponseEntity<UserWithTokenDto> register(@RequestBody UserDto user) {
        User newUser = User.builder()
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .deleteFlag(false)
                .role(Role.USER)
                .build();
        User addedUser = service.createUser(newUser);
        EzeUserDetails userDetails = new EzeUserDetails(addedUser);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return ResponseEntity.ok(new UserWithTokenDto(addedUser.getUsername(), addedUser.getRole(), accessToken, refreshToken));
    }

    // TODO: Add Unit test
    @GetMapping("/users/refresh/{refreshToken}")
    public ResponseEntity<UserWithTokenDto> refreshToken(@PathVariable("refreshToken") String refreshToken){
        String username = jwtUtil.extractUsername(refreshToken);
        User authenticatedUser = service.findUser(username);
        EzeUserDetails userDetails = new EzeUserDetails(authenticatedUser);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        return ResponseEntity.ok(new UserWithTokenDto(authenticatedUser.getUsername(), authenticatedUser.getRole(), accessToken, refreshToken));
    }

    @GetMapping("/users/validate/{accessToken}")
    public ResponseEntity<UserWithRole> validateToken(@PathVariable("accessToken") String accessToken){
        String username = jwtUtil.extractUsername(accessToken);
        User authenticateUser = service.findUser(username);
        return ResponseEntity.ok(new UserWithRole(authenticateUser.getUsername(), authenticateUser.getRole().name()));
    }

    // TODO: Add Unit test
    @PostMapping("/users/{username}/avatar")
    public ResponseEntity<Object> upload(@RequestParam("avatar") MultipartFile avatar,
                                         @PathVariable("username") String username) throws IOException {
        service.addAvatar(username, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{username}/avatar")
    public ResponseEntity<byte[]> download(@PathVariable("username") String username) {
        User user = service.findUser(username);
        return ResponseEntity.ok().header("Content-Type", "image/jpg").body(user.getAvatar());
    }
}
