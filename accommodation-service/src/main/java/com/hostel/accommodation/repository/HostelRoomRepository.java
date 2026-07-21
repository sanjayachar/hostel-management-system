package com.hostel.accommodation.repository;

import com.hostel.accommodation.entity.HostelRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface HostelRoomRepository extends JpaRepository<HostelRoom, Long> {
    @EntityGraph(attributePaths = "hostel")
    List<HostelRoom> findAllByActiveFlagOrderByHostelHostelNameAscRoomNumberAsc(String activeFlag);

    @EntityGraph(attributePaths = "hostel")
    List<HostelRoom> findAllByHostelHostelIdAndActiveFlagOrderByRoomNumberAsc(Long hostelId, String activeFlag);

    @EntityGraph(attributePaths = "hostel")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<HostelRoom> findByRoomIdAndActiveFlag(Long roomId, String activeFlag);
}
