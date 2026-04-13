package com.hostel.management.common.controller;

import com.hostel.management.candidate.dto.CandidateDto;
import com.hostel.management.student.dto.StudentsDto;
import com.hostel.management.candidate.service.CandidateService;
import com.hostel.management.student.service.StudentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/register")
@RequiredArgsConstructor
public class PublicRegisterController {

    private final StudentsService studentService;
    private final CandidateService candidateService;

    @PostMapping("/student")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody StudentsDto studentsDto) {
        studentService.saveOrUpdateStudent(studentsDto);
        return ResponseEntity.ok("Student registered successfully");
    }

    @PostMapping("/candidate")
    public ResponseEntity<?> registerCandidate(@Valid @RequestBody CandidateDto candidateDto) {
        candidateService.saveOrUpdateCandidate(candidateDto);
        return ResponseEntity.ok("Candidate registered successfully");
    }
}