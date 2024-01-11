package com.example.authentication.service;

import com.example.authentication.dto.UpdateUserRoleDto;
import com.example.authentication.dto.UserResponseDto;
import com.example.authentication.entity.User;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDto> getUserList(Integer offset, Integer limit) {
        return userService.listAll(offset, limit);
    }

    public UserResponseDto createNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.createUser(user);
    }

    public UserResponseDto updateRole(UpdateUserRoleDto updateUserRoleDto) {
        return userService.updateRole(updateUserRoleDto);
    }

    public String deleteUser(String username) {
        userService.deleteUser(username);
        return "The user was successfully deleted.";
    }

}
