package com.eze.backend.spring.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.eze.backend.spring.config.SecurityConfiguration;
import com.eze.backend.spring.dtos.EzeUserDetails;
import com.eze.backend.spring.dtos.LoginRequestDto;
import com.eze.backend.spring.dtos.LoginResponseDto;
import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.service.AccountService;
import com.eze.backend.spring.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.security.SecurityConfig;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.time.LocalDateTime;

@WebMvcTest(AccountControllerTest.class)
public class AccountControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AccountService service;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtUtil jwtUtil;


    private ObjectMapper mapper;
    private LoginRequestDto loginRequestDto;
    private EzeUserDetails userDetails;
    private Account account0;

    @BeforeEach
    void setup() {
        loginRequestDto = new LoginRequestDto("Username0", "Password0");
        account0 = new Account("Name0", "Username0", "Email0", "Password0", AccountType.SA, "http://sampleurl.com/profile0", LocalDateTime.now(), true, false);
        userDetails = new EzeUserDetails(account0);
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @DisplayName("Should create MockMvc")
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("Login using invalid credentials")
    void login_usingInvalidCredentials_returns401Unauthorized() throws Exception {
        when(service.loadUserByUsername(loginRequestDto.getUsername())).thenReturn(userDetails);
        when(passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())).thenReturn(false);
        String json = mapper.writeValueAsString(loginRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Login using valid credentials")
    void login_usingValidCredentials_returns200OkWithLoginResponse() throws Exception {
        when(service.loadUserByUsername(loginRequestDto.getUsername())).thenReturn(userDetails);
        when(passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())).thenReturn(true);
        String accessToken = "AccessToken0";
        String refreshToken = "RefreshToken0";
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn(refreshToken);
        when(jwtUtil.generateToken(userDetails)).thenReturn(accessToken);
        LoginResponseDto responseDto = new LoginResponseDto(userDetails.getUsername(), userDetails.getAuthorities().stream().findFirst().get().toString(), userDetails.getFullName(), accessToken, refreshToken, userDetails.getProfile());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(loginRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(responseDto)));
    }
}
