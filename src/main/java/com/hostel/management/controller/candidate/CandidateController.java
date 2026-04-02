package com.hostel.management.controller.candidate;

import com.hostel.management.dto.AccommodationRequestDto;
import com.hostel.management.modal.User;
import com.hostel.management.service.AccommodationService;
import com.hostel.management.util.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final AccommodationService accommodationService;

    @GetMapping("/request/list")
    public ResponseEntity<?> getRequestList() {
        String role = SecurityContextUtil.getRole();
        return ResponseEntity.ok(accommodationService.getRequetList(role));
    }

    @PostMapping("/request")
    public ResponseEntity<?> saveOrUpdateStudentAccommodation(@Valid @RequestBody AccommodationRequestDto accommodationRequestDto) {
        accommodationService.saveOrUpdateAccommodation(accommodationRequestDto);
        return ResponseEntity.ok("Candidate accommodation saved/updated successfully");
    }
}
