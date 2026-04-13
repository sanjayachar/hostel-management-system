package com.hostel.management.student.mapper;

import com.hostel.management.student.dto.StudentsDto;
import com.hostel.management.student.entity.Students;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = ".", source = ".")
    Students toEntity(StudentsDto dto);

    @Mapping(target = ".", source = ".")
    StudentsDto toDto(Students entity);

    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Students toEntityForSave(StudentsDto dto);
}