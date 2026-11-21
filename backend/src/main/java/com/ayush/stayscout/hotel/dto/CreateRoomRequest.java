package com.ayush.stayscout.hotel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateRoomRequest(
        @NotBlank @Size(max = 100) String roomType,
        @DecimalMin(value = "0.01") BigDecimal pricePerNight,
        @Min(1) @Max(20) int capacity
) {
}
