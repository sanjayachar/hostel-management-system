package com.candidate.othercandidateservice.controller;

import com.candidate.othercandidateservice.dto.CandidateDto;
import com.candidate.othercandidateservice.service.CandidateService;
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

    private final CandidateService candidateService;

    @PostMapping("/candidate")
    public ResponseEntity<?> registerCandidate(@Valid @RequestBody CandidateDto candidateDto) {
        candidateService.saveOrUpdateCandidate(candidateDto);
        return ResponseEntity.ok("Candidate registered successfully");
    }

    @GetMapping("/candidate/next-candidate-code")
    public ResponseEntity<?> getNextCandidateCode() {
        return ResponseEntity.ok(candidateService.getNextCandidateCode());
    }
}
