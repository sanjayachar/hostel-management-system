package com.hostel.management.controller.admin;

import com.hostel.management.dto.AccommodationRequestDto;
import com.hostel.management.enums.RoleEnum;
import com.hostel.management.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/request/details")
public class AccommodationRequestController {
    private final AccommodationService accommodationService;

    @GetMapping("/students")
    public ResponseEntity<?> getStudentRequestList(){
        return ResponseEntity.ok(accommodationService.getRequetList(RoleEnum.ROLE_STUDENT));
    }

    @GetMapping("/candidates")
    public ResponseEntity<?> getCandidateRequestList(){
        return ResponseEntity.ok(accommodationService.getRequetList(RoleEnum.ROLE_CANDIDATE));
    }

    @GetMapping("/staffs")
    public ResponseEntity<?> getStaffRequestList(){
        return ResponseEntity.ok(accommodationService.getRequetList(RoleEnum.ROLE_STAFF));
    }

    @PostMapping("/approvalStatus")
    public ResponseEntity<?> approveAccommodationRequest(@RequestBody AccommodationRequestDto accommodationRequestDto) {
        String status = accommodationService.updateAccommodationRequestStatus(accommodationRequestDto);
        return ResponseEntity.ok(status + " successfully.!");
    }
}
