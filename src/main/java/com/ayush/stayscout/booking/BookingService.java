package com.ayush.stayscout.booking;

import com.ayush.stayscout.auth.User;
import com.ayush.stayscout.auth.UserRepository;
import com.ayush.stayscout.booking.dto.BookingResponse;
import com.ayush.stayscout.booking.dto.CreateBookingRequest;
import com.ayush.stayscout.common.BookingConflictException;
import com.ayush.stayscout.common.InvalidRequestException;
import com.ayush.stayscout.common.ResourceNotFoundException;
import com.ayush.stayscout.hotel.Room;
import com.ayush.stayscout.hotel.RoomRepository;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse create(CreateBookingRequest request) {
        if (!request.checkOut().isAfter(request.checkIn())) {
            throw new InvalidRequestException("checkOut must be after checkIn");
        }
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User " + request.userId() + " was not found"));
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room " + request.roomId() + " was not found"));
        long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        BigDecimal total = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        try {
            Booking booking = bookingRepository.saveAndFlush(new Booking(user, room, request.checkIn(), request.checkOut(), total));
            return BookingResponse.from(booking);
        } catch (DataIntegrityViolationException exception) {
            throw new BookingConflictException("Room already booked for these dates");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " was not found"));
        return bookingRepository.findByUserOrderByCreatedAtDesc(user).stream().map(BookingResponse::from).toList();
    }

    @Transactional
    public void cancel(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking " + bookingId + " was not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Booking " + bookingId + " was not found");
        }
        booking.cancel();
    }
}
