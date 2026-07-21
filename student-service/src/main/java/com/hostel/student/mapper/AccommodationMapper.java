package com.hostel.student.mapper;

import com.hostel.proto.accommodation.AccommodationResponse;
import com.hostel.student.dto.AccommodationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    AccommodationRequestDto toDto(AccommodationResponse accommodationResponse);
}
