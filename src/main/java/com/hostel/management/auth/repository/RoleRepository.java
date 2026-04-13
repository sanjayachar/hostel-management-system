package com.hostel.management.auth.repository;

import com.hostel.management.common.enums.RoleEnum;
import com.hostel.management.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleEnum roleName);
}
