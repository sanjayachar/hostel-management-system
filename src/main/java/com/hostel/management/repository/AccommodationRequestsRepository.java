package com.hostel.management.repository;

import com.hostel.management.enums.RoleEnum;
import com.hostel.management.modal.AccommodationRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccommodationRequestsRepository extends JpaRepository<AccommodationRequests, Long> {
    Optional<AccommodationRequests> findByRequestIdAndActiveFlag(Long requestId, String activeFlag);
    Optional<List<AccommodationRequests>> findAllByUserRoleAndActiveFlag(RoleEnum userRole, String activeFlag);
}
