package com.ayush.stayscout.hotel;

import com.ayush.stayscout.common.InvalidRequestException;
import com.ayush.stayscout.common.ResourceNotFoundException;
import com.ayush.stayscout.hotel.dto.HotelDetailResponse;
import com.ayush.stayscout.hotel.dto.HotelSummaryResponse;
import com.ayush.stayscout.hotel.dto.CreateHotelRequest;
import com.ayush.stayscout.hotel.dto.CreateRoomRequest;
import com.ayush.stayscout.hotel.dto.RoomResponse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "hotelSearch", key = "{#location,#minPrice,#maxPrice,#minRating,#page,#size}")
    public Page<HotelSummaryResponse> search(
            String location,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minRating,
            int page,
            int size
    ) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidRequestException("minPrice must be less than or equal to maxPrice");
        }

        Specification<Hotel> specification = (root, query, builder) -> builder.conjunction();
        if (location != null && !location.isBlank()) {
            String requestedLocation = location.trim().toLowerCase(Locale.ROOT);
            final String normalizedLocation = (requestedLocation.equals("bangalore") || requestedLocation.equals("bengaluru"))
                    ? "bengaluru"
                    : requestedLocation;
            final String pattern = "%" + normalizedLocation.replace("%", "\\%").replace("_", "\\_") + "%";
            specification = specification.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.get("city")), pattern, '\\'),
                    builder.like(builder.lower(root.get("country")), pattern, '\\')
            ));
        }
        if (minRating != null) {
            specification = specification.and((root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("rating"), minRating));
        }
        if (minPrice != null || maxPrice != null) {
            specification = specification.and((root, query, builder) -> {
                Join<Hotel, Room> rooms = root.join("rooms", JoinType.INNER);
                query.distinct(true);
                if (minPrice != null && maxPrice != null) {
                    return builder.between(rooms.get("pricePerNight"), minPrice, maxPrice);
                }
                if (minPrice != null) {
                    return builder.greaterThanOrEqualTo(rooms.get("pricePerNight"), minPrice);
                }
                return builder.lessThanOrEqualTo(rooms.get("pricePerNight"), maxPrice);
            });
        }

        return hotelRepository.findAll(
                        specification,
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("rating"), Sort.Order.asc("id")))
                )
                .map(HotelSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public HotelDetailResponse getById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel " + id + " was not found"));
        return HotelDetailResponse.from(hotel);
    }

    @Transactional
    @CacheEvict(cacheNames = "hotelSearch", allEntries = true)
    public HotelSummaryResponse create(CreateHotelRequest request) {
        Hotel hotel = new Hotel(
                request.name().trim(),
                request.city().trim(),
                request.country().trim(),
                request.rating(),
                request.description() == null ? null : request.description().trim()
        );
        return HotelSummaryResponse.from(hotelRepository.save(hotel));
    }

    @Transactional
    @CacheEvict(cacheNames = "hotelSearch", allEntries = true)
    public RoomResponse createRoom(Long hotelId, CreateRoomRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel " + hotelId + " was not found"));
        Room room = new Room(hotel, request.roomType().trim(), request.pricePerNight(), request.capacity());
        return RoomResponse.from(roomRepository.save(room));
    }
}
