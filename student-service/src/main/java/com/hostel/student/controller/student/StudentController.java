package com.hostel.student.controller.student;

import com.hostel.student.common.enums.RoleEnum;
import com.hostel.student.common.util.SecurityContextUtil;
import com.hostel.student.dto.AccommodationRequestDto;
import com.hostel.student.service.StudentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Validated
public class StudentController {

    private final StudentsService studentsService;

    @GetMapping("/request/list")
    public ResponseEntity<?> getRequestList() {
        String role = SecurityContextUtil.getRole();
        RoleEnum roleEnum = RoleEnum.valueOf(role);
        List<AccommodationRequestDto> accommodationRequestDtoList = studentsService.getStudentsAccommodationRequest(roleEnum);
        return ResponseEntity.ok(accommodationRequestDtoList);
    }

    @PostMapping("/saveRequest")
    public ResponseEntity<?> saveOrUpdateStudentAccommodation(@Valid @RequestBody AccommodationRequestDto accommodationRequestDto) {
        ResponseEntity<?> responseEntity = studentsService.saveOrUpdateAccommodation(accommodationRequestDto);
        return ResponseEntity.ok(responseEntity);
    }
}
