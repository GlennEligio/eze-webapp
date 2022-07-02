package com.eze.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.eze.userservice.config.EzeUserDetails;
import com.eze.userservice.domain.Role;
import com.eze.userservice.domain.User;
import com.eze.userservice.exception.ApiException;
import com.eze.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@DisplayName("User Service")
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserServiceImpl userService;

    private User user0;

    @BeforeEach
    void setup(){
        user0 = new User(0L, "name0", "username0", "password0", false, Role.USER);
        User user1 = new User(1L, "name1", "username1", "password1", false, Role.ADMIN);
        User user2 = new User(2L, "name2", "username2", "password2", true, Role.SADMIN);
        when(userRepository.findByDeleteFlagFalse()).thenReturn(List.of(user0, user1));
    }

    @DisplayName("returns Users with deleteFlag false")
    @Test
    void findAllUsers_withUsersWithDeleteFlagFalse_returnUsers(){
        List<User> users = userService.findAllUsers();

        assertNotEquals(0, users.size());
    }

    @DisplayName("returns User with valid Username")
    @Test
    void findUser_withValidUsername_returnsUser(){
        String validUsername = user0.getUsername();
        when(userRepository.findByUsernameAndDeleteFlagFalse(validUsername)).thenReturn(Optional.ofNullable(user0));

        User user = userService.findUser(validUsername);

        assertNotNull(user);
        assertEquals(validUsername, user.getUsername());
    }

    @DisplayName("throws exception if finding User using invalid username")
    @Test
    void findUser_withInvalidUsername_throwsApiException(){
        String invalidUsername = "username99";
        when(userRepository.findByUsernameAndDeleteFlagFalse(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.findUser(invalidUsername));
    }

    @DisplayName("creates an Unexisting User and returns it")
    @Test
    void createUser_withUnexistingUser_returnsCreatedUser(){
        when(userRepository.save(user0)).thenReturn(user0);

        User user = userService.createUser(user0);

        assertNotNull(user);
    }

    @DisplayName("creates an existing User and throws an exception")
    @Test
    void createUser_withExistingUser_throwsApiException(){
        when(userRepository.save(user0)).thenThrow(new ApiException("User already exist", HttpStatus.BAD_REQUEST));

        assertThrows(ApiException.class, () -> userService.createUser(user0));
    }

    // TODO: To include setter function effect in test case
    @DisplayName("updates an existing User and doesnt throw an exception")
    @Test
    void updateUser_withExistingUser_returnsUpdatedUser() {
        String validUsername = user0.getUsername();
        when(userRepository.findByUsernameAndDeleteFlagFalse(validUsername)).thenReturn(Optional.ofNullable(user0));

        assertDoesNotThrow(() -> userService.updateUser(user0));
    }

    @DisplayName("updates an non-existing User and throws an exception")
    @Test
    void updateUser_withNonExistingUser_throwsApiException(){
        String invalidUsername = user0.getUsername();
        when(userRepository.findByUsernameAndDeleteFlagFalse(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.updateUser(user0));
    }

    @DisplayName("deletes an existing User and doesnt throw an exception")
    @Test
    void deleteUser_withExistingUser_doesNotThrowException(){
        String validUsername = user0.getUsername();
        when(userRepository.findByUsernameAndDeleteFlagFalse(validUsername)).thenReturn(Optional.ofNullable(user0));

        assertDoesNotThrow(() -> userService.deleteUser(validUsername));
        assertTrue(userService.deleteUser(validUsername));
    }

    @DisplayName("deletes a non-existent User and throws an exception")
    @Test
    void deleteUser_withNonExistingUser_throwsApiException(){
        String invalidUsername = "username99";
        when(userRepository.findByUsernameAndDeleteFlagFalse(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.deleteUser(invalidUsername));
    }

    @DisplayName("loads an existing User and returns an EzeUserDetails")
    @Test
    void loadByUsername_withValidUsername_returnsAnEzeUserDetails(){
        String validUsername = user0.getUsername();
        when(userRepository.findByUsernameAndDeleteFlagFalse(validUsername)).thenReturn(Optional.of(user0));

        assertEquals(userService.loadUserByUsername(validUsername).getClass(), EzeUserDetails.class);
    }

    @DisplayName("loads a non-existing User and throws an exception")
    @Test
    void loadByUsername_withInvalidUsername_throwsApiException(){
        String validUsername = user0.getUsername();
        when(userRepository.findByUsernameAndDeleteFlagFalse(validUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.loadUserByUsername(validUsername));
    }

    @DisplayName("authenticate valid credential and returns User")
    @Test
    void authenticateUser_withValidCredentials_returnsUser() {
        String validUsername = user0.getUsername();
        String validPassword = user0.getPassword();

        when(userRepository.findByUsernameAndDeleteFlagFalse(validUsername)).thenReturn(Optional.of(user0));
        when(passwordEncoder.matches(validPassword, user0.getPassword())).thenReturn(true);

        assertNotNull(userService.authenticateUser(validUsername, validPassword));
    }

    @DisplayName("authenticate invalid credentials and throws an Exception")
    @Test
    void authenticateUser_withInvalidCredentials_throwsException(){
        String validUsername = user0.getUsername();
        String invalidPassword = user0.getPassword();

        when(userRepository.findByUsernameAndDeleteFlagFalse(validUsername)).thenReturn(Optional.of(user0));
        when(passwordEncoder.matches(invalidPassword, user0.getPassword())).thenReturn(false);

        assertThrows(ApiException.class, () -> userService.authenticateUser(validUsername, invalidPassword));
    }

    @DisplayName("authenticate non-existing User and throws an Exception")
    @Test
    void authenticateUser_withNonExistingUser_throwsException(){
        String invalidUsername = user0.getUsername();
        String validPassword = user0.getPassword();

        when(userRepository.findByUsernameAndDeleteFlagFalse(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.authenticateUser(invalidUsername, validPassword));
    }
}
