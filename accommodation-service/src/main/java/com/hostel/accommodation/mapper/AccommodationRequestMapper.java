package com.hostel.accommodation.mapper;


import com.hostel.accommodation.dto.AccommodationRequestDto;
import com.hostel.accommodation.entity.AccommodationRequests;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccommodationRequestMapper {
    @Mapping(target = ".", source = ".")
    AccommodationRequests toEntity(AccommodationRequestDto dto);

    @Mapping(target = ".", source = ".")
    AccommodationRequestDto toDto(AccommodationRequests entity);
}
