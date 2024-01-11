package com.example.authentication.service;

import com.example.authentication.dto.JwtToken;
import com.example.authentication.dto.LoginDto;
import com.example.authentication.dto.RegisterDto;
import com.example.authentication.dto.UserResponseDto;
import com.example.authentication.entity.User;
import com.example.authentication.exception.UsernameOrPasswordException;
import com.example.authentication.mapper.UserDtoMapper;
import com.example.authentication.utils.JwtTokensUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationServiceUnitTest {

    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokensUtil jwtTokensUtil;

    private static final RegisterDto REGISTER_DTO =
        RegisterDto.builder().username("username").password("password").email("username@mail.ru").build();

    private static final LoginDto LOGIN_DTO = LoginDto.builder().username("username").password("password").build();

    private static final User USER = new User("username", "password", "username@mail.ru");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService =
            new AuthenticationService(
                authenticationManager,
                userService,
                userDtoMapper,
                passwordEncoder,
                jwtTokensUtil
            );
    }

    @Test
    void registerSuccessTest() {
        UserResponseDto expected = UserResponseDto.builder()
            .id(USER.getId())
            .username(USER.getUsername())
            .email(USER.getEmail())
            .role(USER.getRole())
            .build();
        Mockito.when(userDtoMapper.registerDtoToEntity(REGISTER_DTO)).thenReturn(USER);
        Mockito.when(passwordEncoder.encode(USER.getPassword())).thenReturn(USER.getPassword());
        Mockito.when(userService.createUser(USER)).thenReturn(expected);
        Mockito.when(userDtoMapper.entityToResponseDto(USER)).thenReturn(expected);
        UserResponseDto result = authenticationService.register(REGISTER_DTO);
        assertEquals(expected, result);
    }

    @Test
    void loginSuccessTest() {
        JwtToken expected = new JwtToken("All right!");
        Mockito.when(userService.findByUsername(LOGIN_DTO.getUsername())).thenReturn(USER);
        Mockito.when(jwtTokensUtil.generateToken(USER)).thenReturn(expected.getToken());
        JwtToken result = authenticationService.login(LOGIN_DTO);
        assertEquals(expected, result);
    }

    @Test
    void loginBadCredentialTest() {
        Mockito.when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                LOGIN_DTO.getUsername(),
                LOGIN_DTO.getPassword()
            ))).thenThrow(BadCredentialsException.class);
        Throwable thrown = assertThrows(UsernameOrPasswordException.class, () -> authenticationService.login(LOGIN_DTO));
        assertNotNull(thrown.getMessage());
    }

}
