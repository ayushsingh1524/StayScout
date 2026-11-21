package com.ayush.stayscout.hotel;

import com.ayush.stayscout.hotel.dto.CreateHotelRequest;
import com.ayush.stayscout.hotel.dto.HotelSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
