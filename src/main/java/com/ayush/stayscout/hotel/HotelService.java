package com.ayush.stayscout.hotel;

import com.ayush.stayscout.common.InvalidRequestException;
import com.ayush.stayscout.common.ResourceNotFoundException;
import com.ayush.stayscout.hotel.dto.HotelDetailResponse;
import com.ayush.stayscout.hotel.dto.HotelSummaryResponse;
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

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "hotelSearch", key = "{#city,#minPrice,#maxPrice,#minRating,#page,#size}")
    public Page<HotelSummaryResponse> search(
            String city,
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
        if (city != null && !city.isBlank()) {
            String normalizedCity = city.trim().toLowerCase(Locale.ROOT);
            specification = specification.and((root, query, builder) ->
                    builder.equal(builder.lower(root.get("city")), normalizedCity));
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
}
