package com.rapidx.auth.service;

import com.rapidx.auth.dto.LoginRequestDto;
import com.rapidx.auth.dto.LoginResponseDto;
import com.rapidx.auth.dto.RegisterRequestDto;
import com.rapidx.auth.dto.UserDto;
import com.rapidx.auth.entity.Role;
import com.rapidx.auth.entity.RoleName;
import com.rapidx.auth.entity.User;
import com.rapidx.auth.processor.UserProcessor;
import com.rapidx.auth.repository.RoleRepository;
import com.rapidx.auth.repository.UserRepository;
import com.rapidx.auth.security.JwtTokenProvider;
import com.rapidx.auth.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserProcessor userProcessor;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            UserProcessor userProcessor) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userProcessor = userProcessor;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new LoginResponseDto(jwt, userDetails.getUsername(), roles);
    }

    @Override
    public UserDto register(RegisterRequestDto registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        // Create new user's account
        User user = new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail()
        );

        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        return userProcessor.transformToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto validateAndGetUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid or missing Authorization header");
        }

        String token = authorizationHeader.substring(7);
        if (!tokenProvider.validateJwtToken(token)) {
            throw new BadCredentialsException("Invalid or expired JWT token");
        }

        String username = tokenProvider.getUsernameFromJwtToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return userProcessor.transformToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> filterUsers(String username, String email, String role, Pageable pageable) {
        Specification<User> spec = UserSpecification.filterUsers(username, email, role);
        Page<User> userPage = userRepository.findAll(spec, pageable);
        return userPage.map(userProcessor::transformToDto);
    }

    // Custom runtime exception helper inner class for user service loading
    private static class UsernameNotFoundException extends RuntimeException {
        public UsernameNotFoundException(String msg) {
            super(msg);
        }
    }
}
