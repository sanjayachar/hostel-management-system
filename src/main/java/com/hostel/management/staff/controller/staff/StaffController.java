package com.hostel.management.staff.controller.staff;

import com.hostel.management.accommodation.dto.AccommodationRequestDto;
import com.hostel.management.common.enums.RoleEnum;
import com.hostel.management.accommodation.service.AccommodationService;
import com.hostel.management.common.util.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final AccommodationService accommodationService;

    @PostMapping("/request")
    public ResponseEntity<?> saveOrUpdateStudentAccommodation(@Valid @RequestBody AccommodationRequestDto accommodationRequestDto) {
        accommodationService.saveOrUpdateAccommodation(accommodationRequestDto);
        return ResponseEntity.ok("Staff accommodation saved/updated successfully");
    }

    @GetMapping("/request/list")
    public ResponseEntity<?> getRequestList() {
        String role = SecurityContextUtil.getRole();
        RoleEnum roleEnum = RoleEnum.valueOf(role);
        return ResponseEntity.ok(accommodationService.getRequetList(roleEnum));
    }
}
