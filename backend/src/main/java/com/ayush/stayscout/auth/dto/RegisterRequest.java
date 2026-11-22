package com.ayush.stayscout.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank
        @Pattern(
                regexp = "(?=.*[A-Za-z])(?=.*\\d).{8,}",
                message = "must be at least 8 characters and include a letter and a number"
        )
        String password
) { }
