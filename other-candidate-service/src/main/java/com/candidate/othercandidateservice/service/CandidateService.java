package com.candidate.othercandidateservice.service;

import com.candidate.othercandidateservice.client.grpc.AccommodationClient;
import com.candidate.othercandidateservice.client.grpc.AuthClient;
import com.candidate.othercandidateservice.common.enums.RoleEnum;
import com.candidate.othercandidateservice.common.exception.ResourceNotFoundException;
import com.candidate.othercandidateservice.common.util.Constants;
import com.candidate.othercandidateservice.common.util.SecurityContextUtil;
import com.candidate.othercandidateservice.dto.AccommodationRequestDto;
import com.candidate.othercandidateservice.dto.CandidateDto;
import com.candidate.othercandidateservice.entity.Candidate;
import com.candidate.othercandidateservice.mapper.CandidateMapper;
import com.candidate.othercandidateservice.repository.CandidateRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {
    private final CandidateRepo candidateRepo;
    private final CandidateMapper candidateMapper;
    private final AuthClient authClient;
    private final AccommodationClient accommodationClient;

    public void saveOrUpdateCandidate(@Valid CandidateDto candidateDto) {
        if (candidateDto.getCandidateId() == null) {
            registerCandidate(candidateDto);
        } else {
            updateCandidate(candidateDto);
        }
    }

    public void registerCandidate(@Valid CandidateDto candidateDto) {
        Long userId = null;
        try {
            userId = saveUserDetails(candidateDto);
            Candidate candidate = candidateMapper.toEntityForSave(candidateDto);
            candidate.setUserId(userId);
            candidateRepo.save(candidate);
        } catch (Exception e) {
            if (userId != null) {
                try {
                    authClient.deleteUser(userId);
                    log.info("Rollback success for user {}", userId);
                } catch (Exception rollbackEx) {
                    log.error("Rollback failed for user {}", userId, rollbackEx);
                }
            }
            throw new RuntimeException("Candidate registration failed", e);
        }
    }

    public void updateCandidate(@Valid CandidateDto candidateDto) {
        Candidate candidate = candidateRepo.findById(candidateDto.getCandidateId()).orElseThrow(() -> new RuntimeException("Candidate not found"));
        candidate.setFirstName(candidateDto.getFirstName());
        candidate.setLastName(candidateDto.getLastName());
        candidate.setGender(candidateDto.getGender());
        candidate.setDateOfBirth(candidateDto.getDateOfBirth());
        candidate.setContactNumber(candidateDto.getContactNumber());
        candidate.setEmail(candidateDto.getEmail());
        candidate.setAddress(candidateDto.getAddress());
        candidate.setCity(candidateDto.getCity());
        candidate.setState(candidateDto.getState());
        candidate.setPinCode(candidateDto.getPinCode());
        candidate.setAppliedPost(candidateDto.getAppliedPost());
        candidateRepo.save(candidate);
    }

    private Long saveUserDetails(@Valid CandidateDto candidateDto) {
        String rawPassword = generatePassword();
        log.info("The new raw password for user {} is {}", candidateDto.getCandidateCode(), rawPassword);
        return authClient.createUser(
                candidateDto.getCandidateCode(),
                rawPassword,
                RoleEnum.ROLE_CANDIDATE.name()
        );
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public List<CandidateDto> getCandidateList() {
        return candidateRepo.findAll().stream().map(candidateMapper::toDto).collect(Collectors.toList());
    }

    public CandidateDto getCandidateById(Long candidateId) {
        return candidateRepo.findByCandidateIdAndActiveFlag(candidateId, Constants.ACTIVE)
                .map(candidateMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate Not Found"));
    }

    public CandidateDto getCandidateByCandidateCode(String candidateCode) {
        return candidateRepo.findByCandidateCodeAndActiveFlag(candidateCode, Constants.ACTIVE)
                .map(candidateMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate Not Found"));
    }

    public List<AccommodationRequestDto> getCandidateAccommodationRequest(RoleEnum roleEnum) {
        return accommodationClient.getRequests(roleEnum.name(), SecurityContextUtil.getUserId());
    }

    public String getNextCandidateCode() {
        String prefix = "CAND" + Year.now().getValue();
        Integer maxSuffix = candidateRepo.findMaxCandidateCodeSuffix(prefix);
        int nextSuffix = (maxSuffix == null ? 0 : maxSuffix) + 1;
        return prefix + String.format("%03d", nextSuffix);
    }

    public ResponseEntity<?> saveOrUpdateAccommodation(@Valid AccommodationRequestDto accommodationRequestDto) {
        String token = SecurityContextUtil.getToken();
        Long userId = SecurityContextUtil.getUserId();
        String userRole = SecurityContextUtil.getRole();
        accommodationRequestDto.setUserId(userId);
        accommodationRequestDto.setUserRole(RoleEnum.valueOf(userRole));
        return ResponseEntity.ok(accommodationClient.saveRequest(accommodationRequestDto, token));
    }
}
