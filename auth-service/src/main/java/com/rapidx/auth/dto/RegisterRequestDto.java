package com.rapidx.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDto {
    private String username;
    private String password;
    private String email;
    private Set<String> roles;
}
