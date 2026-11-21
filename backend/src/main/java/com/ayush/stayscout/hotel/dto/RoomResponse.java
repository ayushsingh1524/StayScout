package com.ayush.stayscout.hotel.dto;

import com.ayush.stayscout.hotel.Room;
import java.math.BigDecimal;

public record RoomResponse(
        Long id,
        String roomType,
        BigDecimal pricePerNight,
        int capacity
) {
    public static RoomResponse from(Room room) {
        return new RoomResponse(room.getId(), room.getRoomType(), room.getPricePerNight(), room.getCapacity());
    }
}
