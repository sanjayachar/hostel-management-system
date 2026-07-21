package com.hostel.accommodation.controller;

import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.dto.AccommodationRequestDto;
import com.hostel.accommodation.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/request")
@RequiredArgsConstructor
@Validated
public class StudentAccommodationRequestController {

    private final AccommodationService accommodationService;

    @GetMapping("/list")
    public ResponseEntity<List<AccommodationRequestDto>> getRequests(@RequestParam String role) {
        RoleEnum roleEnum = RoleEnum.valueOf(role);
        return ResponseEntity.ok(accommodationService.getRequetList(roleEnum));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestHeader("Authorization") String token, @Valid @RequestBody AccommodationRequestDto dto) {
        accommodationService.saveOrUpdateAccommodation(dto, token);
        return ResponseEntity.ok("Saved");
    }
}
