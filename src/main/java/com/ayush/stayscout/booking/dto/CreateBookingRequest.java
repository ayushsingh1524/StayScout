package com.ayush.stayscout.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateBookingRequest(
        @NotNull Long userId,
        @NotNull Long roomId,
        @NotNull @FutureOrPresent LocalDate checkIn,
        @NotNull @FutureOrPresent LocalDate checkOut
) { }
