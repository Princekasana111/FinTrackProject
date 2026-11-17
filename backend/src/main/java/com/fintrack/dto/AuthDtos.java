package com.fintrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Auth-related request/response shapes, grouped in one file for convenience.
 * Records are immutable data carriers — perfect for DTOs.
 */
public class AuthDtos {

    public record RegisterRequest(
            @NotBlank(message = "Name is required")
            String name,

            @Email(message = "Must be a valid email")
            @NotBlank(message = "Email is required")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 6, message = "Password must be at least 6 characters")
            String password
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    /** What we send back after a successful register/login. */
    public record AuthResponse(
            String token,
            Long userId,
            String name,
            String email,
            String role
    ) {}
}
