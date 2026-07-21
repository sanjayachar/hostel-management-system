package com.candidate.othercandidateservice.repository;

import com.candidate.othercandidateservice.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateRepo extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByCandidateIdAndActiveFlag(Long candidateId, String activeFlag);
    Optional<Candidate> findByCandidateCodeAndActiveFlag(String candidateCode, String activeFlag);
    Optional<Candidate> findFirstByUserIdAndActiveFlagOrderByCandidateIdDesc(Long userId, String activeFlag);

    List<Candidate> findAllByActiveFlag(String activeFlag);

    List<Candidate> findAllByCandidateCodeStartingWith(String prefix);
}
