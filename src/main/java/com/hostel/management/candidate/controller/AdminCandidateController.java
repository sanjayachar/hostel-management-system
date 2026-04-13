package com.hostel.management.candidate.controller;

import com.hostel.management.candidate.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/candidates")
public class AdminCandidateController {

    private final CandidateService candidateService;

    @GetMapping("/list")
    public ResponseEntity<?> getCandidateList(){
        return ResponseEntity.ok(candidateService.getCandidateList());
    }

    @GetMapping("/view/{candidateId}")
    public ResponseEntity<?> getCandidateDetails(@PathVariable Long candidateId) {
        return ResponseEntity.ok(candidateService.getCandidateById(candidateId));
    }
}
