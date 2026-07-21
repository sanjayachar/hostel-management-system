package com.hostel.staff.controller;

import com.hostel.staff.common.enums.RoleEnum;
import com.hostel.staff.service.AdminStudentService;
import com.hostel.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {
    private final AdminStudentService adminStudentService;
    private final StaffService staffService;

    @GetMapping("/list")
    public ResponseEntity<?> listStudents() {
        return ResponseEntity.ok(adminStudentService.getAllStudents());
    }

    @GetMapping("/view/{studentId}")
    public ResponseEntity<?> getStudentDetailsById(@PathVariable Long studentId) {
        return ResponseEntity.ok(adminStudentService.getStudentById(studentId));
    }

    @GetMapping("/request/list")
    public ResponseEntity<?> getRequestList() {
        RoleEnum roleEnum = RoleEnum.ROLE_STUDENT;
        return ResponseEntity.ok(staffService.getStaffsAccommodationRequest(roleEnum));
    }
}
