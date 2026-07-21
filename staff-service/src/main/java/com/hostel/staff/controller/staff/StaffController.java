package com.hostel.staff.controller.staff;

import com.hostel.staff.common.enums.RoleEnum;
import com.hostel.staff.common.util.SecurityContextUtil;
import com.hostel.staff.dto.AccommodationRequestDto;
import com.hostel.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping("/request")
    public ResponseEntity<?> saveOrUpdateStudentAccommodation(@Valid @RequestBody AccommodationRequestDto accommodationRequestDto) {
        staffService.saveOrUpdateAccommodation(accommodationRequestDto);
        return ResponseEntity.ok("Staff accommodation saved/updated successfully");
    }

    @GetMapping("/request/list")
    public ResponseEntity<?> getRequestList() {
        String role = SecurityContextUtil.getRole();
        RoleEnum roleEnum = RoleEnum.valueOf(role);
        return ResponseEntity.ok(staffService.getStaffsAccommodationRequest(roleEnum));
    }
}
