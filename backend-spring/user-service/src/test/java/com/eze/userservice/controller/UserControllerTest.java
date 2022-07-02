package com.eze.userservice.controller;

import com.eze.userservice.config.EzeUserDetails;
import com.eze.userservice.domain.Role;
import com.eze.userservice.domain.User;
import com.eze.userservice.dto.LoginUserDto;
import com.eze.userservice.dto.UserWithRole;
import com.eze.userservice.dto.UserWithTokenDto;
import com.eze.userservice.exception.ApiException;
import com.eze.userservice.service.UserServiceImpl;
import com.eze.userservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UserController.class)
@DisplayName("User Controller")
class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private User user0;
    private List<User> users;
    private static final String BASE_URI = "/api/v1/users/";
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        user0 = new User(0L, "name0", "username0", "password0", false, Role.USER);
        User user1 = new User(1L, "name1", "username1", "password1", false, Role.ADMIN);
        User user2 = new User(2L, "name2", "username2", "password2", true, Role.SADMIN);
        users = List.of(user0, user1, user2);
        mapper = new ObjectMapper();
    }

    @DisplayName("Fetching List of User returns 200 OK")
    @WithMockUser(roles = "USER")
    @Test
    void getAllUsers_withExistingUsers_returnOk() throws Exception {
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Fetching existing User and returns 200 OK with the User")
    @WithMockUser(roles = "USER")
    @Test
    void getUser_withExistingUser_returnsOk() throws Exception {
        when(userService.findUser(user0.getUsername())).thenReturn(user0);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + user0.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(user0)));
    }

    @DisplayName("Fetching non-existing User and returns 404 NOT FOUND")
    @WithMockUser(roles = "USER")
    @Test
    void getUser_withNonExistingUser_returnsNotFound() throws Exception {
        when(userService.findUser(user0.getUsername())).thenThrow(new ApiException("User not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + user0.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Create new User and returns 201 CREATED with the User")
    @WithMockUser(roles = "ADMIN")
    @Test
    void createUser_withNewUser_returnsCreated() throws Exception {
        when(userService.createUser(user0)).thenReturn(user0);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user0)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(user0)));
    }

    @DisplayName("Create existing User and returns 400 BAD REQUEST")
    @WithMockUser(roles = "ADMIN")
    @Test
    void createUser_withExistingUser_returnsBadRequest() throws Exception {
        when(userService.createUser(user0)).thenThrow(new ApiException("User already exist", HttpStatus.BAD_REQUEST));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user0)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("Update existing User and returns 200 OK")
    @WithMockUser(roles = "ADMIN")
    @Test
    void updateUser_withExistingUser_returnsOk() throws Exception {
        when(userService.updateUser(user0)).thenReturn(user0);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user0)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Update non-existing User and returns 200 OK")
    @WithMockUser(roles = "ADMIN")
    @Test
    void updateUser_withNonExistingUser_returnsNotFound() throws Exception {
        when(userService.updateUser(user0)).thenThrow(new ApiException("User doesnt exist", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user0)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Delete existing User and returns 200 OK")
    @WithMockUser(roles = "ADMIN")
    @Test
    void deleteUser_withExistingUser_returnsOk() throws Exception {
        when(userService.deleteUser(user0.getUsername())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + user0.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Delete non-existing User and returns 200 OK")
    @WithMockUser(roles = "ADMIN")
    @Test
    void deleteUser_withNonExistingUser_returnsNotFound() throws Exception {
        when(userService.deleteUser(user0.getUsername())).thenThrow(new ApiException("User not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + user0.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Login with valid credentials and returns 200 OK with tokens")
    @WithMockUser(roles = "ADMIN")
    @Test
    void login_withValidCredentials_returnOk() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto(user0.getUsername(), user0.getPassword());
        String accessToken = "someRandomAccessToken";
        String refreshToken = "someRefreshToken";
        UserWithTokenDto userWithTokenDto = new UserWithTokenDto(user0.getUsername(), user0.getRole(), accessToken, refreshToken);
        when(userService.authenticateUser(user0.getUsername(), user0.getPassword())).thenReturn(user0);
        when(jwtUtil.generateAccessToken(new EzeUserDetails(user0))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(new EzeUserDetails(user0))).thenReturn(refreshToken);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI + "login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(userWithTokenDto)));
    }

    @DisplayName("Login with invalid credentials and return 401 UNAUTHORIZED")
    @WithMockUser(roles = "ADMIN")
    @Test
    void login_withInvalidCredentials_returnUnauthorized() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto(user0.getUsername(), user0.getPassword());
        when(userService.authenticateUser(user0.getUsername(), user0.getPassword())).thenThrow(new ApiException("User not found", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI + "login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @DisplayName("Refresh token with valid token and returns 200 OK with new access token")
    @Test
    void refreshToken_withValidToken_returnOk() throws Exception {
        String accessToken = "someRandomAccessToken";
        String refreshToken = "someRefreshToken";
        UserWithTokenDto userWithTokenDto = new UserWithTokenDto(user0.getUsername(), user0.getRole(), accessToken, refreshToken);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn(user0.getUsername());
        when(jwtUtil.generateAccessToken(new EzeUserDetails(user0))).thenReturn(accessToken);
        when(userService.findUser(user0.getUsername())).thenReturn(user0);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "refresh/" + refreshToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // TODO: Handler exceptions thrown by JwtParser.parseClaimsJwt in the UserExceptionResponseHandler.class
    @DisplayName("Refresh token with invalid token and returns 401 UNAUTHORIZED")
    @Test
    void refreshToken_withInvalidToken_returnOk() throws Exception {
        String refreshToken = "someRefreshToken";
        when(jwtUtil.extractUsername(refreshToken)).thenThrow(new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "refresh/" + refreshToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @DisplayName("Validate a valid token and return 200 OK with username and role")
    @Test
    void validateToken_withValidToken_returnsOk() throws Exception {
        String accessToken = "someRandomToken";
        when(jwtUtil.extractUsername(accessToken)).thenReturn(user0.getUsername());
        when(userService.findUser(user0.getUsername())).thenReturn(user0);
        UserWithRole userDto = new UserWithRole(user0.getUsername(), user0.getRole().name());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "validate/" + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(userDto)));
    }

    @DisplayName("Validate an invalid token and return 401 UNAUTHORIZED")
    @Test
    void validateToken_withInvalidToken_returnsOk() throws Exception {
        String accessToken = "someRandomToken";
        when(jwtUtil.extractUsername(accessToken)).thenThrow(new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "validate/" + accessToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
