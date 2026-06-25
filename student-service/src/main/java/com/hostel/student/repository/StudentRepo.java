package com.hostel.student.repository;

import com.hostel.student.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepo extends JpaRepository<Students, Long> {
    Optional<Students> findByStudentIdAndActiveFlag(Long studentId, String active);
    Optional<Students> findByAdmissionNumberAndActiveFlag(String admissionNumber, String active);
    Optional<List<Students>> findAllByActiveFlag(String active);

    @Query(value = """
            select coalesce(max(cast(substring(admission_number from length(:prefix) + 1) as integer)), 0)
            from hostel.students
            where admission_number like concat(:prefix, '%')
            """, nativeQuery = true)
    Integer findMaxAdmissionNumberSuffix(@Param("prefix") String prefix);
}
