package com.hostel.staff.mapper;

import com.hostel.staff.dto.StaffDto;
import com.hostel.staff.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    @Mapping(target = ".", source = ".")
    Staff toEntity(StaffDto dto);

    @Mapping(target = ".", source = ".")
    StaffDto toDto(Staff entity);

    @Mapping(target = "staffId", ignore = true)
    Staff toEntityForSave(StaffDto dto);
}
