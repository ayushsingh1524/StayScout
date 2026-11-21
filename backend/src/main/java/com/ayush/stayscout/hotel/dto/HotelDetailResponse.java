package com.ayush.stayscout.hotel.dto;

import com.ayush.stayscout.hotel.Hotel;
import java.math.BigDecimal;
import java.util.List;

public record HotelDetailResponse(
        Long id,
        String name,
        String city,
        String country,
        BigDecimal rating,
        String description,
        List<RoomResponse> rooms
) {
    public static HotelDetailResponse from(Hotel hotel) {
        return new HotelDetailResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getCountry(),
                hotel.getRating(),
                hotel.getDescription(),
                hotel.getRooms().stream().map(RoomResponse::from).toList()
        );
    }
}
