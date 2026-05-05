package com.hostel.student.mapper;

import com.hostel.proto.accommodation.AccommodationResponse;
import com.hostel.student.dto.AccommodationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccommodationMapper {

    @Mapping(source = "id", target = "requestId")
    @Mapping(source = "room", target = "status")
    AccommodationRequestDto toDto(AccommodationResponse accommodationResponse);
}
