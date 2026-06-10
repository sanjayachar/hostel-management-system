package com.hostel.staff.service;

import com.hostel.staff.client.grpc.StudentDetailsClient;
import com.hostel.staff.common.enums.RoleEnum;
import com.hostel.staff.dto.StudentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStudentService {

    private final StudentDetailsClient studentDetailsClient;
    public List<StudentsDto> getAllStudents() {
        RoleEnum roleEnum = RoleEnum.ROLE_STUDENT;
        return studentDetailsClient.getRequests(roleEnum.name());
    }

    public StudentsDto getStudentById(Long studentId) {
        RoleEnum roleEnum = RoleEnum.ROLE_STUDENT;
        return studentDetailsClient.getRequest(roleEnum.name(), studentId);
    }
}
