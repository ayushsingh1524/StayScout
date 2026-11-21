package com.ayush.stayscout.booking;

import com.ayush.stayscout.booking.dto.BookingResponse;
import com.ayush.stayscout.booking.dto.CreateBookingRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;
    public BookingController(BookingService bookingService) { this.bookingService = bookingService; }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.create(request);
        return ResponseEntity.created(URI.create("/bookings/" + response.id())).body(response);
    }
    @GetMapping
    public List<BookingResponse> list(@RequestParam @Min(1) Long userId) { return bookingService.findByUser(userId); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable @Min(1) Long id, @RequestParam @Min(1) Long userId) {
        bookingService.cancel(id, userId); return ResponseEntity.noContent().build();
    }
}
