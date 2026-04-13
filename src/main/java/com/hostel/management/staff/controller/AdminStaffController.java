package com.hostel.management.staff.controller;

import com.hostel.management.staff.dto.StaffDto;
import com.hostel.management.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/staffs")
@RequiredArgsConstructor
public class AdminStaffController {

    private final StaffService staffService;

    @PostMapping("/register")
    public ResponseEntity<?> saveOrUpdateStaff(@Valid @RequestBody StaffDto staffDto) {
        staffService.saveOrUpdateStaff(staffDto);
        return ResponseEntity.ok("Staff saved successfully..!");
    }

    @GetMapping("/list")
    public ResponseEntity<?> listStaff() {
        return ResponseEntity.ok(staffService.getAllStaffs());
    }

    @GetMapping("/view/{staffId}")
    public ResponseEntity<?> getStaffDetails(@PathVariable Long staffId) {
        return ResponseEntity.ok(staffService.getStaffById(staffId));
    }
}
