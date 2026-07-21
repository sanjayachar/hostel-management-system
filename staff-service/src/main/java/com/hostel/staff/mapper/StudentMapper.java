package com.hostel.staff.mapper;

import com.hostel.proto.student_details.StudentDetailsResponse;
import com.hostel.staff.dto.StudentsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentsDto fromStudentDetailsResponse(StudentDetailsResponse studentDetailsResponse);
}
