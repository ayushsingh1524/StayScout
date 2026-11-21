package com.ayush.stayscout.booking.dto;

import com.ayush.stayscout.booking.Booking;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingResponse(Long id, Long roomId, LocalDate checkIn, LocalDate checkOut, String status, BigDecimal totalPrice) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getRoom().getId(), booking.getCheckIn(), booking.getCheckOut(),
                booking.getStatus().name(), booking.getTotalPrice());
    }
}
