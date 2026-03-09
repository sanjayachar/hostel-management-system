package com.hostel.management.repository;

import com.hostel.management.modal.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepo extends JpaRepository<Students, Long> {
}
