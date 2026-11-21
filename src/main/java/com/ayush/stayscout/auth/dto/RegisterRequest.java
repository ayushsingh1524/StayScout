package com.ayush.stayscout.auth.dto; import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank @Email String email,@NotBlank @Size(min=8) String password){}
