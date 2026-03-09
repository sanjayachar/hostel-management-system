package com.hostel.management.controller;

import com.hostel.management.dto.StudentsDto;
import com.hostel.management.service.StudentsService;
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

    @PostMapping("/student")
    public ResponseEntity<String> saveOrUpdateStudent(@RequestBody StudentsDto studentsDto) {
        studentService.saveOrUpdateStudent(studentsDto);
        return ResponseEntity.ok("Student saved successfully..!");
    }
}
