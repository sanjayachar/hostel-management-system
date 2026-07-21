package com.candidate.othercandidateservice.mapper;

import com.candidate.othercandidateservice.dto.AccommodationRequestDto;
import com.hostel.proto.accommodation.AccommodationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    AccommodationRequestDto toDto(AccommodationResponse accommodationResponse);
}
