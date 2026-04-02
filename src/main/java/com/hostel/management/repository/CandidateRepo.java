package com.hostel.management.repository;

import com.hostel.management.modal.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepo extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByCandidateIdAndActiveFlag(Long candidateId, String activeFlag);
    Optional<Candidate> findByCandidateCodeAndActiveFlag(String candidateCode, String activeFlag);
}
