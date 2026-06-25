package com.candidate.othercandidateservice.controller.candidate;

import com.candidate.othercandidateservice.common.enums.RoleEnum;
import com.candidate.othercandidateservice.common.util.SecurityContextUtil;
import com.candidate.othercandidateservice.dto.AccommodationRequestDto;
import com.candidate.othercandidateservice.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidate")
@RequiredArgsConstructor
@Validated
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping("/request/list")
    public ResponseEntity<?> getRequestList() {
        String role = SecurityContextUtil.getRole();
        RoleEnum roleEnum = RoleEnum.valueOf(role);
        List<AccommodationRequestDto> accommodationRequestDtoList = candidateService.getCandidateAccommodationRequest(roleEnum);
        return ResponseEntity.ok(accommodationRequestDtoList);
    }

    @PostMapping("/saveRequest")
    public ResponseEntity<?> saveOrUpdateCandidateAccommodation(@Valid @RequestBody AccommodationRequestDto accommodationRequestDto) {
        ResponseEntity<?> responseEntity = candidateService.saveOrUpdateAccommodation(accommodationRequestDto);
        return ResponseEntity.ok(responseEntity);
    }
}
