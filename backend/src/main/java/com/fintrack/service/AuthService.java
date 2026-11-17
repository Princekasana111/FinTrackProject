package com.fintrack.service;

import com.fintrack.dto.AuthDtos.*;
import com.fintrack.entity.Role;
import com.fintrack.entity.User;
import com.fintrack.exception.ApiException;
import com.fintrack.repository.UserRepository;
import com.fintrack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles registration and login. On success it returns a JWT the frontend
 * stores and sends on every subsequent request.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw ApiException.conflict("An account with this email already exists");
        }

        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password())) // hash, never store plain
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return toResponse(token, user);
    }

    public AuthResponse login(LoginRequest req) {
        // This throws BadCredentialsException if the password is wrong.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        String token = jwtService.generateToken(user.getEmail());
        return toResponse(token, user);
    }

    private AuthResponse toResponse(String token, User user) {
        return new AuthResponse(token, user.getId(), user.getName(),
                user.getEmail(), user.getRole().name());
    }
}
