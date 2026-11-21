package com.ayush.stayscout.hotel.dto;

import com.ayush.stayscout.hotel.Hotel;
import java.math.BigDecimal;

public record HotelSummaryResponse(
        Long id,
        String name,
        String city,
        String country,
        BigDecimal rating
) {
    public static HotelSummaryResponse from(Hotel hotel) {
        return new HotelSummaryResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getCountry(),
                hotel.getRating()
        );
    }
}
