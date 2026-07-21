package com.hostel.accommodation.repository;

import com.hostel.accommodation.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HostelRepository extends JpaRepository<Hostel, Long> {
    List<Hostel> findAllByActiveFlagOrderByHostelNameAsc(String activeFlag);

    Optional<Hostel> findByHostelIdAndActiveFlag(Long hostelId, String activeFlag);

    boolean existsByHostelCodeIgnoreCaseAndActiveFlag(String hostelCode, String activeFlag);
}
