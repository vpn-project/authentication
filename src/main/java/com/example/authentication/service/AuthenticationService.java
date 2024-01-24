package com.example.authentication.service;

import com.example.authentication.dto.*;
import com.example.authentication.entity.User;
import com.example.authentication.exception.UsernameOrPasswordException;
import com.example.authentication.mapper.UserDtoMapper;
import com.example.authentication.utils.JwtTokensUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokensUtil jwtTokensUtil;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public UserResponseDto register(RegisterDto dto) {
        User user = userDtoMapper.registerDtoToEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.createUser(user);
    }

    public JwtToken login(LoginDto dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUsername(),
                            dto.getPassword()
                    ));
        } catch (BadCredentialsException e) {
            throw new UsernameOrPasswordException();
        }

        User user = userService.findByUsername(dto.getUsername());
        try {
            kafkaTemplate.send("notification_topic",objectMapper.writeValueAsString(new NotificationDto(user.getUsername(), user.getEmail())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new JwtToken(jwtTokensUtil.generateToken(user));
    }

}
