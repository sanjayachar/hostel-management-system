package com.candidate.othercandidateservice.repository;

import com.candidate.othercandidateservice.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CandidateRepo extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByCandidateIdAndActiveFlag(Long candidateId, String activeFlag);
    Optional<Candidate> findByCandidateCodeAndActiveFlag(String candidateCode, String activeFlag);

    @Query(value = """
            select coalesce(max(cast(substring(candidate_code from length(:prefix) + 1) as integer)), 0)
            from hostel.candidates
            where candidate_code like concat(:prefix, '%')
            """, nativeQuery = true)
    Integer findMaxCandidateCodeSuffix(@Param("prefix") String prefix);
}
