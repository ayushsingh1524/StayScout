package com.ayush.stayscout.hotel;

import com.ayush.stayscout.hotel.dto.CreateHotelRequest;
import com.ayush.stayscout.hotel.dto.HotelSummaryResponse;
import com.ayush.stayscout.hotel.dto.CreateRoomRequest;
import com.ayush.stayscout.hotel.dto.RoomResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/hotels")
public class AdminHotelController {

    private final HotelService hotelService;

    public AdminHotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelSummaryResponse create(@Valid @RequestBody CreateHotelRequest request) {
        return hotelService.create(request);
    }

    @PostMapping("/{hotelId}/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@PathVariable @Min(1) Long hotelId, @Valid @RequestBody CreateRoomRequest request) {
        return hotelService.createRoom(hotelId, request);
    }
}
