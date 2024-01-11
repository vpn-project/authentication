package com.example.authentication.service;

import com.example.authentication.dto.UpdateUserRoleDto;
import com.example.authentication.dto.UserResponseDto;
import com.example.authentication.entity.Role;
import com.example.authentication.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminServiceUnitTest {

    private AdminService adminService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final User USER = new User("username", "password", "username@mail.ru");

    private static final UserResponseDto USER_RESPONSE_DTO =
        UserResponseDto.builder().id(USER.getId()).username(USER.getUsername()).email(USER.getEmail())
            .role(USER.getRole()).build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminService(userService, passwordEncoder);
    }

    @Test
    void createNewUserTest() {
        Mockito.when(passwordEncoder.encode(USER.getPassword())).thenReturn(USER.getPassword());
        Mockito.when(userService.createUser(USER)).thenReturn(USER_RESPONSE_DTO);
        UserResponseDto result = adminService.createNewUser(USER);
        assertEquals(USER.getUsername(), result.getUsername());
        assertEquals(USER.getEmail(), result.getEmail());
        assertEquals(USER.getRole(), result.getRole());
    }

    @Test
    void updateRoleTest() {
        UpdateUserRoleDto dto = new UpdateUserRoleDto(USER.getUsername(), Role.ADMIN);
        Mockito.when(userService.updateRole(dto)).thenReturn(USER_RESPONSE_DTO);
        UserResponseDto result = adminService.updateRole(dto);
        assertEquals(USER_RESPONSE_DTO, result);
    }

    @Test
    void deleteUserTest() {
        String result = adminService.deleteUser(USER.getUsername());
        assertEquals("The user was successfully deleted.", result);
    }

}
