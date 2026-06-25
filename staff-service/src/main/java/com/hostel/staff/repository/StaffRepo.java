package com.hostel.staff.repository;

import com.hostel.staff.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StaffRepo extends JpaRepository<Staff, Long> {
    Optional<Staff> findByStaffIdAndActiveFlag(Long staffId, String active);
    Optional<Staff> findByEmployeeCodeAndActiveFlag(String employeeCode, String active);

    @Query(value = """
            select coalesce(max(cast(substring(employee_code from length(:prefix) + 1) as integer)), 0)
            from hostel.staff
            where employee_code like concat(:prefix, '%')
            """, nativeQuery = true)
    Integer findMaxEmployeeCodeSuffix(@Param("prefix") String prefix);
}
