package com.example.authentication.service;

import com.example.authentication.dto.JwtToken;
import com.example.authentication.dto.LoginDto;
import com.example.authentication.dto.RegisterDto;
import com.example.authentication.dto.UserResponseDto;
import com.example.authentication.entity.User;
import com.example.authentication.exception.UsernameOrPasswordException;
import com.example.authentication.mapper.UserDtoMapper;
import com.example.authentication.utils.JwtTokensUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokensUtil jwtTokensUtil;

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
        return new JwtToken(jwtTokensUtil.generateToken(user));
    }

}
