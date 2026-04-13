package com.hostel.management.accommodation.mapper;

import com.hostel.management.accommodation.dto.AccommodationRequestDto;
import com.hostel.management.accommodation.entity.AccommodationRequests;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccommodationRequestMapper {
    @Mapping(target = ".", source = ".")
    AccommodationRequests toEntity(AccommodationRequestDto dto);

    @Mapping(target = ".", source = ".")
    AccommodationRequestDto toDto(AccommodationRequests entity);

    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "user", ignore = true)
    AccommodationRequests toEntityForSave(AccommodationRequestDto dto);
}
