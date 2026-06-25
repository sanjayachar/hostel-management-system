package com.candidate.othercandidateservice.controller;

import com.candidate.othercandidateservice.service.CandidateService;
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
    public ResponseEntity<?> listCandidates(){
        return ResponseEntity.ok(candidateService.getCandidateList());
    }

    @GetMapping("/view/{candidateId}")
    public ResponseEntity<?> getCandidateDetailsById(@PathVariable Long candidateId) {
        return ResponseEntity.ok(candidateService.getCandidateById(candidateId));
    }
}
