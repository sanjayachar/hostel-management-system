package com.hostel.accommodation.controller;

import com.hostel.accommodation.dto.HostelDto;
import com.hostel.accommodation.dto.HostelRoomDto;
import com.hostel.accommodation.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminHostelController {

    private final AccommodationService accommodationService;

    @GetMapping("/hostels")
    public ResponseEntity<?> getHostels() {
        return ResponseEntity.ok(accommodationService.getHostels());
    }

    @PostMapping("/hostels")
    public ResponseEntity<?> createHostel(@Valid @RequestBody HostelDto hostelDto) {
        return ResponseEntity.ok(accommodationService.createHostel(hostelDto));
    }

    @GetMapping("/hostel-rooms")
    public ResponseEntity<?> getRooms(@RequestParam(required = false) Long hostelId) {
        return ResponseEntity.ok(accommodationService.getRooms(hostelId));
    }

    @PostMapping("/hostel-rooms")
    public ResponseEntity<?> createRoom(@Valid @RequestBody HostelRoomDto roomDto) {
        return ResponseEntity.ok(accommodationService.createRoom(roomDto));
    }
}
