package com.hostel.management.controller;

import com.hostel.management.dto.CandidateDto;
import com.hostel.management.dto.StaffDto;
import com.hostel.management.dto.StudentsDto;
import com.hostel.management.service.CandidateService;
import com.hostel.management.service.StaffService;
import com.hostel.management.service.StudentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private final StudentsService studentService;
    private final StaffService staffService;
    private final CandidateService candidateService;

    @PostMapping("/student")
    public ResponseEntity<?> saveOrUpdateStudent(@Valid @RequestBody StudentsDto studentsDto) {
        studentService.saveOrUpdateStudent(studentsDto);
        return ResponseEntity.ok("Student saved successfully..!");
    }

    @PostMapping("/staff")
    public ResponseEntity<?> saveOrUpdateStaff(@Valid @RequestBody StaffDto staffDto) {
        staffService.saveOrUpdateStaff(staffDto);
        return ResponseEntity.ok("Staff saved successfully..!");
    }

    @PostMapping("/candidate")
    public ResponseEntity<?> saveOrUpdateCandidate(@Valid @RequestBody CandidateDto candidateDto) {
        candidateService.saveOrUpdateCandidate(candidateDto);
        return ResponseEntity.ok("Candidate saved successfully..!");
    }
}
