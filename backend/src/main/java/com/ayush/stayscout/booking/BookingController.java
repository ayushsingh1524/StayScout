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
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;
    public BookingController(BookingService bookingService) { this.bookingService = bookingService; }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request, @AuthenticationPrincipal String userId) {
        BookingResponse response = bookingService.create(request, Long.valueOf(userId));
        return ResponseEntity.created(URI.create("/bookings/" + response.id())).body(response);
    }
    @GetMapping
    public List<BookingResponse> list(@AuthenticationPrincipal String userId) { return bookingService.findByUser(Long.valueOf(userId)); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable @Min(1) Long id, @AuthenticationPrincipal String userId) {
        bookingService.cancel(id, Long.valueOf(userId)); return ResponseEntity.noContent().build();
    }
}
