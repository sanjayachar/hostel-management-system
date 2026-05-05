package com.hostel.accommodation.repository;

import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.entity.AccommodationRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccommodationRequestsRepository extends JpaRepository<AccommodationRequests, Long> {
    Optional<AccommodationRequests> findByRequestIdAndActiveFlag(Long requestId, String activeFlag);
    Optional<List<AccommodationRequests>> findAllByUserRoleAndActiveFlag(RoleEnum userRole, String activeFlag);
}
