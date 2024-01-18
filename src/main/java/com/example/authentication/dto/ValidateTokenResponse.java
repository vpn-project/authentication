package com.example.authentication.dto;

import com.example.authentication.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTokenResponse {

    String login;
    List<Role> authorities;
    boolean isAuthenticated;
}
