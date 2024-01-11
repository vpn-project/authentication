package com.example.authentication.controller;

import com.example.authentication.AuthenticationApplication;
import com.example.authentication.dto.LoginDto;
import com.example.authentication.dto.RegisterDto;
import com.example.authentication.dto.UserResponseDto;
import com.example.authentication.entity.User;
import com.example.authentication.service.AuthenticationService;
import com.example.authentication.utils.JwtTokensUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = AuthenticationApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokensUtil jwtTokensUtil;

    @Autowired
    private AuthenticationService authenticationService;

    private static final RegisterDto REGISTER_DTO =
        RegisterDto.builder().username("username").password("password").email("username@mail.ru").build();

    private static final LoginDto LOGIN_DTO = LoginDto.builder().username("username").password("password").build();

    private static final User USER = new User("username", "password", "username@mail.ru");

    @Test
    public void testRegisterAndLogin() throws Exception {
        MvcResult registerResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(REGISTER_DTO)))
            .andExpect(status().isCreated())
            .andReturn();

        UserResponseDto userResponseDto =
            objectMapper.readValue(registerResult.getResponse().getContentAsString(), UserResponseDto.class);

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LOGIN_DTO)))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        LoginDto loginDto = new LoginDto("invalidUser", "invalidPassword");
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();
    }
}
