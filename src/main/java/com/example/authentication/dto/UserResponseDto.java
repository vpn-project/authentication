package com.example.authentication.dto;

import com.example.authentication.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class UserResponseDto {

    @Min(1)
    private int id;

    @Size(min = 5, message = "The username cannot be shortest than 5 characters!")
    @Size(max = 32, message = "The username cannot be longer than 32 characters!")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "The username can contain only letters!")
    private String username;

    @Email
    @NotBlank(message = "The email cannot be empty!")
    private String email;

    @NotBlank
    private Role role;

}
