package com.hostel.staff.repository;

import com.hostel.staff.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepo extends JpaRepository<Staff, Long> {
    Optional<Staff> findByStaffIdAndActiveFlag(Long staffId, String active);
    Optional<Staff> findByEmployeeCodeAndActiveFlag(String employeeCode, String active);
    Optional<Staff> findFirstByUserIdAndActiveFlagOrderByStaffIdDesc(Long userId, String active);

    List<Staff> findAllByActiveFlag(String active);

    List<Staff> findAllByEmployeeCodeStartingWith(String prefix);
}
