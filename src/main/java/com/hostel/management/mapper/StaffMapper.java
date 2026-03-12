package com.hostel.management.mapper;

import com.hostel.management.dto.StaffDto;
import com.hostel.management.dto.StudentsDto;
import com.hostel.management.modal.Staff;
import com.hostel.management.modal.Students;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    @Mapping(target = ".", source = ".")
    Staff toEntity(StaffDto dto);

    @Mapping(target = ".", source = ".")
    StaffDto toDto(Staff entity);

    @Mapping(target = "staffId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Staff toEntityForSave(StaffDto dto);
}
