package com.rapidx.auth;

import com.rapidx.auth.dto.LoginRequestDto;
import com.rapidx.auth.dto.LoginResponseDto;
import com.rapidx.auth.dto.RegisterRequestDto;
import com.rapidx.auth.dto.UserDto;
import com.rapidx.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AuthApplicationTests {

    @Autowired
    private AuthService authService;

    @Test
    void contextLoads() {
        // Simple context check
    }

    @Test
    void testRegisterAndLoginFlow() {
        // 1. Register a new user
        RegisterRequestDto registerRequest = new RegisterRequestDto();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("testpassword");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setRoles(Collections.singleton("user"));

        UserDto userDto = authService.register(registerRequest);
        assertThat(userDto).isNotNull();
        assertThat(userDto.getUsername()).isEqualTo("testuser");
        assertThat(userDto.getEmail()).isEqualTo("testuser@example.com");
        assertThat(userDto.getRoles()).contains("ROLE_USER");

        // 2. Login with valid credentials
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpassword");

        LoginResponseDto loginResponse = authService.login(loginRequest);
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getToken()).isNotBlank();
        assertThat(loginResponse.getUsername()).isEqualTo("testuser");
        assertThat(loginResponse.getRoles()).contains("ROLE_USER");

        // 3. Validate Token
        String authHeader = "Bearer " + loginResponse.getToken();
        UserDto validatedUser = authService.validateAndGetUser(authHeader);
        assertThat(validatedUser).isNotNull();
        assertThat(validatedUser.getUsername()).isEqualTo("testuser");
        assertThat(validatedUser.getEmail()).isEqualTo("testuser@example.com");
        assertThat(validatedUser.getRoles()).contains("ROLE_USER");
    }

    @Test
    void testLoginWithBadCredentials() {
        LoginRequestDto badLogin = new LoginRequestDto();
        badLogin.setUsername("admin");
        badLogin.setPassword("wrongpassword");

        assertThatThrownBy(() -> authService.login(badLogin))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void testFilterUsers() {
        // 1. Register a test user for filtering
        RegisterRequestDto registerRequest = new RegisterRequestDto();
        registerRequest.setUsername("filteruser");
        registerRequest.setPassword("filterpassword");
        registerRequest.setEmail("filteruser@example.com");
        registerRequest.setRoles(Collections.singleton("admin"));

        UserDto userDto = authService.register(registerRequest);
        assertThat(userDto).isNotNull();

        // 2. Filter by username (like match)
        Page<UserDto> filterByName = authService.filterUsers("filter", null, null, PageRequest.of(0, 10));
        assertThat(filterByName.getContent()).isNotEmpty();
        assertThat(filterByName.getContent().stream().anyMatch(u -> u.getUsername().equals("filteruser"))).isTrue();

        // 3. Filter by email (like match)
        Page<UserDto> filterByEmail = authService.filterUsers(null, "filteruser@example.com", null, PageRequest.of(0, 10));
        assertThat(filterByEmail.getContent()).isNotEmpty();
        assertThat(filterByEmail.getContent().stream().anyMatch(u -> u.getEmail().equals("filteruser@example.com"))).isTrue();

        // 4. Filter by roleName
        Page<UserDto> filterByRole = authService.filterUsers(null, null, "admin", PageRequest.of(0, 10));
        assertThat(filterByRole.getContent()).isNotEmpty();
        assertThat(filterByRole.getContent().stream().anyMatch(u -> u.getUsername().equals("filteruser"))).isTrue();
    }
}
