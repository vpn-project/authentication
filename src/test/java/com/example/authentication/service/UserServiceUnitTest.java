package com.example.authentication.service;

import java.util.Optional;

import com.example.authentication.dto.UpdateUserRoleDto;
import com.example.authentication.dto.UserResponseDto;
import com.example.authentication.entity.Role;
import com.example.authentication.entity.User;
import com.example.authentication.exception.EmailAlreadyExistsException;
import com.example.authentication.exception.UserNotFoundException;
import com.example.authentication.exception.UsernameAlreadyExistsException;
import com.example.authentication.exception.UsernameOrPasswordException;
import com.example.authentication.mapper.UserDtoMapper;
import com.example.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceUnitTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoMapper mapper;

    private static final User USER =
        User.builder().id(1).username("username").password("password").email("username@mail.ru").role(Role.USER)
            .build();

    private static final UserResponseDto USER_RESPONSE_DTO =
        UserResponseDto.builder().id(USER.getId()).username(USER.getUsername()).email(USER.getEmail())
            .role(USER.getRole()).build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, mapper);
    }

    @Test
    void createUserSuccessTest() {
        Mockito.when(mapper.entityToResponseDto(USER)).thenReturn(USER_RESPONSE_DTO);
        Mockito.when(userRepository.save(USER)).thenReturn(USER);
        UserResponseDto result = userService.createUser(USER);
        assertEquals(USER_RESPONSE_DTO, result);
    }

    @Test
    void createUserUsernameAlreadyExistsTest() {
        Mockito.when(userService.findByUsername(USER.getUsername())).thenReturn(USER);
        Throwable thrown =
            assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(USER));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void createUserEmailAlreadyExistsTest() {
        Mockito.when(userService.findByUsername(USER.getUsername())).thenReturn(null);
        Mockito.when(userService.findByEmail(USER.getEmail())).thenReturn(USER);
        Throwable thrown =
            assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(USER));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateRoleSuccessTest() {
        UpdateUserRoleDto updateUserRoleDto = new UpdateUserRoleDto("username", Role.USER);
        Mockito.when(userService.findByUsername(updateUserRoleDto.getUsername())).thenReturn(USER);
        Mockito.when(userRepository.save(USER)).thenReturn(USER);
        Mockito.when(mapper.entityToResponseDto(USER)).thenReturn(USER_RESPONSE_DTO);
        UserResponseDto result = userService.updateRole(updateUserRoleDto);
        assertEquals(USER_RESPONSE_DTO, result);
    }

    @Test
    void updateRoleUserNotFoundTest() {
        UpdateUserRoleDto updateUserRoleDto = new UpdateUserRoleDto("username", Role.USER);
        Mockito.when(userService.findByUsername(updateUserRoleDto.getUsername())).thenReturn(null);
        Throwable thrown =
            assertThrows(UserNotFoundException.class, () -> userService.updateRole(updateUserRoleDto));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void findByUsernameTest() {
        User user = USER;
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Optional<User> result = Optional.ofNullable(userService.findByUsername(user.getUsername()));
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findByEmailTest() {
        User user = USER;
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        Optional<User> result = Optional.ofNullable(userService.findByEmail(user.getEmail()));
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void deleteUserUserNotFoundTest() {
        Mockito.when(userService.findByUsername(USER.getUsername())).thenReturn(null);
        Throwable thrown = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(USER.getUsername()));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void loadUserByUsernameSuccessTest() {
        Mockito.when(userService.findByUsername(USER.getUsername())).thenReturn(USER);
        UserDetails user = userService.loadUserByUsername(USER.getUsername());
        assertEquals(USER, user);
    }

    @Test
    void loadUserByUsernameUsernameOrPasswordTest() {
        Mockito.when(userService.findByUsername(USER.getUsername())).thenReturn(null);
        Throwable thrown =
            assertThrows(UsernameOrPasswordException.class, () -> userService.loadUserByUsername(USER.getUsername()));
        assertNotNull(thrown.getMessage());
    }

}
