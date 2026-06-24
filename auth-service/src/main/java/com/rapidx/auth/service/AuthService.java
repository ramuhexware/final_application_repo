package com.rapidx.auth.service;

import com.rapidx.auth.dto.LoginRequestDto;
import com.rapidx.auth.dto.LoginResponseDto;
import com.rapidx.auth.dto.RegisterRequestDto;
import com.rapidx.auth.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto loginRequest);
    UserDto register(RegisterRequestDto registerRequest);
    UserDto validateAndGetUser(String authorizationHeader);
    Page<UserDto> filterUsers(String username, String email, String role, Pageable pageable);
}
