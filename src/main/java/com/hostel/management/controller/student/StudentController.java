package com.hostel.management.controller.student;

import com.hostel.management.dto.AccommodationRequestDto;
import com.hostel.management.enums.RoleEnum;
import com.hostel.management.modal.User;
import com.hostel.management.service.AccommodationService;
import com.hostel.management.service.StudentsService;
import com.hostel.management.util.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final AccommodationService accommodationService;


    @GetMapping("/request/list")
    public ResponseEntity<?> getRequestList() {
        String role = SecurityContextUtil.getRole();
        RoleEnum roleEnum = RoleEnum.valueOf(role);
        return ResponseEntity.ok(accommodationService.getRequetList(roleEnum));
    }

    @PostMapping("/request")
    public ResponseEntity<?> saveOrUpdateStudentAccommodation(@Valid @RequestBody AccommodationRequestDto accommodationRequestDto) {
        accommodationService.saveOrUpdateAccommodation(accommodationRequestDto);
        return ResponseEntity.ok("Student accommodation saved/updated successfully");
    }
}
