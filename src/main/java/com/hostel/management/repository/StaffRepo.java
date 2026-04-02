package com.hostel.management.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.hostel.management.modal.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepo extends JpaRepository<Staff, Long> {
    Optional<Staff> findByStaffIdAndActiveFlag(Long staffId, String active);
    Optional<Staff> findByEmployeeCodeAndActiveFlag(String employeeCode, String active);
}
