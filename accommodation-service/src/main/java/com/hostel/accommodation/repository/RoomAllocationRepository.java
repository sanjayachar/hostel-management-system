package com.hostel.accommodation.repository;

import com.hostel.accommodation.entity.RoomAllocation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomAllocationRepository extends JpaRepository<RoomAllocation, Long> {
    @EntityGraph(attributePaths = {"hostel", "room"})
    Optional<RoomAllocation> findByRequestRequestIdAndAllocationStatus(Long requestId, String allocationStatus);
}
