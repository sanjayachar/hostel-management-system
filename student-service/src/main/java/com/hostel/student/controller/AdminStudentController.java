package com.hostel.student.controller;

import com.hostel.student.service.StudentsService;import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {
    private final StudentsService studentService;

    @GetMapping("/list")
    public ResponseEntity<?> listStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/view/{studentId}")
    public ResponseEntity<?> getStudentDetailsById(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }
}
