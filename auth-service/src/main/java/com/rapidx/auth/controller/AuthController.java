package com.rapidx.auth.controller;

import com.rapidx.auth.dto.LoginRequestDto;
import com.rapidx.auth.dto.LoginResponseDto;
import com.rapidx.auth.dto.RegisterRequestDto;
import com.rapidx.auth.dto.UserDto;
import com.rapidx.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "Endpoints for managing logins, registrations, and token verifications")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequestDto registerRequest) {
        UserDto registeredUser = authService.register(registerRequest);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and issue JWT")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate Authorization JWT and return User details")
    public ResponseEntity<UserDto> validate(@RequestHeader("Authorization") String authorizationHeader) {
        UserDto userDto = authService.validateAndGetUser(authorizationHeader);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users/filter")
    @Operation(summary = "Get users using dynamic filtering (JPA Specifications)")
    public ResponseEntity<Page<UserDto>> filterUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserDto> page = authService.filterUsers(username, email, role, pageable);
        return ResponseEntity.ok(page);
    }
}
