package com.hostel.management.controller.staff;

import com.hostel.management.dto.AccommodationRequestDto;
import com.hostel.management.service.AccommodationService;
import com.hostel.management.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
