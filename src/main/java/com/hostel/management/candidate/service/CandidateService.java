package com.hostel.management.candidate.service;

import com.hostel.management.candidate.dto.CandidateDto;
import com.hostel.management.common.enums.RoleEnum;
import com.hostel.management.common.exception.ResourceNotFoundException;
import com.hostel.management.candidate.mapper.CandidateMapper;
import com.hostel.management.candidate.entity.Candidate;
import com.hostel.management.auth.entity.Role;
import com.hostel.management.auth.entity.User;
import com.hostel.management.candidate.repository.CandidateRepo;
import com.hostel.management.auth.repository.RoleRepository;
import com.hostel.management.auth.repository.UserRepository;
import com.hostel.management.common.util.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {
    private final CandidateRepo candidateRepo;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CandidateMapper candidateMapper;
    private final PasswordEncoder passwordEncoder;

    public void saveOrUpdateCandidate(@Valid CandidateDto candidateDto) {
        if (candidateDto.getCandidateId()==null) {
            registerCandidate(candidateDto);
        } else {
            updateCandidate(candidateDto);
        }
    }
    public void registerCandidate(@Valid CandidateDto candidateDto) {
        User user = saveUserDetails(candidateDto);
        Candidate candidate = candidateMapper.toEntityForSave(candidateDto);
        candidate.setUser(user);
        candidateRepo.save(candidate);
        /* future mail service */
    }

    public void updateCandidate(@Valid CandidateDto candidateDto) {
        Candidate candidate = candidateRepo.findById(candidateDto.getCandidateId()).orElseThrow(()->new RuntimeException("Student Now Found"));
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

    private User saveUserDetails(@Valid CandidateDto candidateDto) {
        User user = new User();
        String rawPassword = generatePassword();
        log.info("The new raw password for user {} is {}",candidateDto.getCandidateCode(), rawPassword);
        Role role = roleRepository.findByRoleName(RoleEnum.ROLE_CANDIDATE).orElseThrow(()->new RuntimeException("Role not found"));
        user.setUsername(candidateDto.getCandidateCode());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public List<CandidateDto> getCandidateList() {
        return candidateRepo.findAll().stream().map(candidateMapper::toDto).collect(Collectors.toList());
    }

    public CandidateDto getCandidateById(Long candidateId) {
        CandidateDto candidateDto = candidateRepo.findByCandidateIdAndActiveFlag(candidateId, Constants.ACTIVE).map(candidateMapper::toDto).orElseThrow(()->new ResourceNotFoundException("Candidate Not Found"));
        return candidateDto;
    }

    public CandidateDto getCandidateByCandidateCode(String candidateCode) {
        CandidateDto candidateDto = candidateRepo.findByCandidateCodeAndActiveFlag(candidateCode, Constants.ACTIVE).map(candidateMapper::toDto).orElseThrow(()->new ResourceNotFoundException("Candidate Not Found"));
        return candidateDto;
    }
}
