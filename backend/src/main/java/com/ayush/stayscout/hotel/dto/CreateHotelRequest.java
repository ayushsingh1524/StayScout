package com.ayush.stayscout.hotel.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateHotelRequest(
        @NotBlank @Size(max = 140) String name,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String country,
        @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal rating,
        @Size(max = 2000) String description
) {
}
