package com.ayush.stayscout.hotel.dto;

import com.ayush.stayscout.hotel.Hotel;
import java.io.Serializable;
import java.math.BigDecimal;

public record HotelSummaryResponse(
        Long id,
        String name,
        String city,
        String country,
        BigDecimal rating,
        String imageUrl
) implements Serializable {
    public static HotelSummaryResponse from(Hotel hotel) {
        return new HotelSummaryResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getCountry(),
                hotel.getRating(),
                hotel.getImageUrl()
        );
    }
}
