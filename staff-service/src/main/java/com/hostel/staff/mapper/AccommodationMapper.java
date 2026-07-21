package com.hostel.staff.mapper;

import com.hostel.proto.accommodation.AccommodationResponse;
import com.hostel.staff.dto.AccommodationRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    AccommodationRequestDto toDto(AccommodationResponse accommodationResponse);
}
