package com.ayush.stayscout.hotel;

import com.ayush.stayscout.hotel.dto.HotelDetailResponse;
import com.ayush.stayscout.hotel.dto.HotelSummaryResponse;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotels")
@Validated
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public Page<HotelSummaryResponse> search(
            @RequestParam(required = false) @Size(max = 100) String location,
            @RequestParam(required = false) @Size(max = 100) String city,
            @RequestParam(required = false) @DecimalMin(value = "0.01") BigDecimal minPrice,
            @RequestParam(required = false) @DecimalMin(value = "0.01") BigDecimal maxPrice,
            @RequestParam(required = false) @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal minRating,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return hotelService.search(location != null ? location : city, minPrice, maxPrice, minRating, page, size);
    }

    @GetMapping("/{id}")
    public HotelDetailResponse getById(@PathVariable @Min(1) Long id) {
        return hotelService.getById(id);
    }
}
