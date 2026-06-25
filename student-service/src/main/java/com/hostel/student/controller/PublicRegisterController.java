package com.hostel.student.controller;

import com.hostel.student.dto.StudentsDto;
import com.hostel.student.service.StudentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/student")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody StudentsDto studentsDto) {
        studentService.saveOrUpdateStudent(studentsDto);
        return ResponseEntity.ok("Student registered successfully");
    }

    @GetMapping("/student/next-admission-number")
    public ResponseEntity<?> getNextAdmissionNumber() {
        return ResponseEntity.ok(studentService.getNextAdmissionNumber());
    }
}
