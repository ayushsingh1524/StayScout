package com.ayush.stayscout.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleCredentialRequest(@NotBlank String credential) { }
