package com.hostel.management.repository;

import com.hostel.management.modal.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
