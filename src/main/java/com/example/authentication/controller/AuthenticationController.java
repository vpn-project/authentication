package com.example.authentication.controller;

import com.example.authentication.dto.LoginDto;
import com.example.authentication.dto.RegisterDto;
import com.example.authentication.dto.ValidateTokenResponse;
import com.example.authentication.entity.Role;
import com.example.authentication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated RegisterDto request) {
        return new ResponseEntity<>(authenticationService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginDto request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validateToken(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        if (username == null) return ResponseEntity.ok(ValidateTokenResponse.builder().isAuthenticated(false).build());
        List<Role> authorities = (List<Role>) httpServletRequest.getAttribute("authorities");
        return ResponseEntity.ok(
                ValidateTokenResponse.builder()
                        .login(username)
                        .authorities(authorities)
                        .isAuthenticated(true)
                        .build()
        );

    }

}
