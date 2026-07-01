package com.hostel.student.repository;

import com.hostel.student.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepo extends JpaRepository<Students, Long> {
    Optional<Students> findByStudentIdAndActiveFlag(Long studentId, String active);
    Optional<Students> findByAdmissionNumberAndActiveFlag(String admissionNumber, String active);
    Optional<Students> findFirstByUserIdAndActiveFlagOrderByStudentIdDesc(Long userId, String active);
    Optional<List<Students>> findAllByActiveFlag(String active);

    List<Students> findAllByAdmissionNumberStartingWith(String prefix);
}
