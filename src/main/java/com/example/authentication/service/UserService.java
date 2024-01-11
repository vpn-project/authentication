package com.example.authentication.service;

import com.example.authentication.dto.UpdateUserRoleDto;
import com.example.authentication.dto.UserResponseDto;
import com.example.authentication.entity.User;
import com.example.authentication.exception.EmailAlreadyExistsException;
import com.example.authentication.exception.UserNotFoundException;
import com.example.authentication.exception.UsernameAlreadyExistsException;
import com.example.authentication.exception.UsernameOrPasswordException;
import com.example.authentication.mapper.UserDtoMapper;
import com.example.authentication.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UserResponseDto createUser(User user) {
        if (Optional.ofNullable(findByUsername(user.getUsername())).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }
        if (Optional.ofNullable(findByEmail(user.getEmail())).isPresent()) {
            throw new EmailAlreadyExistsException();
        }
        return userDtoMapper.entityToResponseDto(userRepository.save(user));
    }

    public UserResponseDto updateRole(UpdateUserRoleDto updateUserRoleDto) {
        User user = findByUsername(updateUserRoleDto.getUsername());
        if (Optional.ofNullable(user).isEmpty()) {
            throw new UserNotFoundException();
        }
        user.setRole(updateUserRoleDto.getRole());
        return userDtoMapper.entityToResponseDto(userRepository.save(user));
    }

    public List<UserResponseDto> listAll(Integer offset, Integer limit) {
        Page<User> userList = userRepository.findAll(PageRequest.of(offset, limit));
        List<UserResponseDto> responseList = new LinkedList<>();
        for (User user : userList) {
            responseList.add(userDtoMapper.entityToResponseDto(user));
        }
        return responseList;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(String username) {
        User user = findByUsername(username);
        if (Optional.ofNullable(user).isEmpty()) {
            throw new UserNotFoundException();
        }
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = Optional.ofNullable(findByUsername(username));
        if (user.isEmpty()) {
            throw new UsernameOrPasswordException();
        }
        return user.get();
    }
}
