package com.hostel.accommodation.service;


import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.common.exception.ResourceNotFoundException;
import com.hostel.accommodation.common.util.Constants;
import com.hostel.accommodation.dto.AccommodationRequestDto;
import com.hostel.accommodation.dto.HostelDto;
import com.hostel.accommodation.dto.HostelRoomDto;
import com.hostel.accommodation.dto.RequestDecisionDto;
import com.hostel.accommodation.dto.RequesterProfileDto;
import com.hostel.accommodation.entity.AccommodationRequests;
import com.hostel.accommodation.entity.Hostel;
import com.hostel.accommodation.entity.HostelRoom;
import com.hostel.accommodation.entity.RoomAllocation;
import com.hostel.accommodation.mapper.AccommodationRequestMapper;
import com.hostel.accommodation.repository.AccommodationRequestsRepository;
import com.hostel.accommodation.repository.HostelRepository;
import com.hostel.accommodation.repository.HostelRoomRepository;
import com.hostel.accommodation.repository.RoomAllocationRepository;
import com.hostel.accommodation.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRequestsRepository accommodationRequestsRepository;
    private final AccommodationRequestMapper accommodationRequestMapper;
    private final HostelRepository hostelRepository;
    private final HostelRoomRepository hostelRoomRepository;
    private final RoomAllocationRepository roomAllocationRepository;
    private final RequesterProfileService requesterProfileService;

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_APPROVED = "Approved";
    private static final String STATUS_REJECTED = "Rejected";
    private static final String ALLOCATION_ACTIVE = "ACTIVE";

    @Transactional
    public void saveOrUpdateAccommodation(AccommodationRequestDto accommodationRequestDto, String token) {
        if (accommodationRequestDto.getRequestId()==null) {
            saveAccommodationRequest(accommodationRequestDto);
        } else {
            updateAccommodationRequest(accommodationRequestDto);
        }
    }

    private void updateAccommodationRequest(AccommodationRequestDto accommodationRequestDto) {
        AccommodationRequests accommodationRequests = accommodationRequestsRepository
                .findByRequestIdAndActiveFlag(accommodationRequestDto.getRequestId(), Constants.ACTIVE)
                .orElseThrow(()->new ResourceNotFoundException("Accommodation Request Not found."));
        accommodationRequests.setRequestType(accommodationRequestDto.getRequestType());
        accommodationRequests.setReason(accommodationRequestDto.getReason());
        accommodationRequests.setFromDate(accommodationRequestDto.getFromDate());
        accommodationRequests.setToDate(accommodationRequestDto.getToDate());
        accommodationRequests.setNoOfDays(accommodationRequestDto.getNoOfDays());
        accommodationRequests.setNoOfPersons(accommodationRequestDto.getNoOfPersons());
        accommodationRequestsRepository.save(accommodationRequests);
    }

    private void saveAccommodationRequest(AccommodationRequestDto dto) {
        AccommodationRequests request = new AccommodationRequests();
        request.setRequestType(dto.getRequestType());
        request.setReason(dto.getReason());
        request.setFromDate(dto.getFromDate());
        request.setToDate(dto.getToDate());
        request.setNoOfDays(dto.getNoOfDays());
        request.setNoOfPersons(dto.getNoOfPersons());
        request.setStatus(STATUS_PENDING);
        request.setUserId(dto.getUserId());
        request.setUserRole(RoleEnum.valueOf(dto.getUserRole().name()));
        accommodationRequestsRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<AccommodationRequestDto> getRequetList(RoleEnum roleEnum) {
        return accommodationRequestsRepository.findAllByUserRoleAndActiveFlag(roleEnum, Constants.ACTIVE)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toRequestDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccommodationRequestDto> getRequestListByUser(RoleEnum roleEnum, Long userId) {
        return accommodationRequestsRepository.findAllByUserRoleAndUserIdAndActiveFlag(roleEnum, userId, Constants.ACTIVE)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toRequestDto)
                .toList();
    }

    @Transactional
    public String updateAccommodationRequestStatus(AccommodationRequestDto accommodationRequestDto) {
        RequestDecisionDto decisionDto = new RequestDecisionDto();
        decisionDto.setRequestId(accommodationRequestDto.getRequestId());
        decisionDto.setStatus(accommodationRequestDto.getStatus());
        decisionDto.setDecisionNote(accommodationRequestDto.getDecisionNote());
        decideAccommodationRequest(decisionDto);
        return normalizeDecisionStatus(accommodationRequestDto.getStatus());
    }

    @Transactional
    public AccommodationRequestDto decideAccommodationRequest(RequestDecisionDto decisionDto) {
        AccommodationRequests accommodationRequests = accommodationRequestsRepository
                .findByRequestIdAndActiveFlag(decisionDto.getRequestId(), Constants.ACTIVE)
                .orElseThrow(()->new ResourceNotFoundException("Accommodation Request Not found."));

        if (!STATUS_PENDING.equalsIgnoreCase(accommodationRequests.getStatus())) {
            throw new RuntimeException("Only pending requests can be approved or rejected.");
        }

        String status = normalizeDecisionStatus(decisionDto.getStatus());

        if (STATUS_APPROVED.equals(status)) {
            approveRequest(accommodationRequests, decisionDto);
        } else if (STATUS_REJECTED.equals(status)) {
            rejectRequest(accommodationRequests, decisionDto);
        } else {
            throw new RuntimeException("Only Approved or Rejected decision is allowed.");
        }

        return toRequestDto(accommodationRequests);
    }

    @Transactional
    public HostelDto createHostel(HostelDto dto) {
        String hostelCode = dto.getHostelCode().trim();

        if (hostelRepository.existsByHostelCodeIgnoreCaseAndActiveFlag(hostelCode, Constants.ACTIVE)) {
            throw new RuntimeException("Hostel code already exists.");
        }

        Hostel hostel = new Hostel();
        hostel.setHostelCode(hostelCode);
        hostel.setHostelName(dto.getHostelName().trim());
        hostel.setHostelType(dto.getHostelType().trim());
        hostel.setAddress(dto.getAddress() == null ? null : dto.getAddress().trim());

        return toHostelDto(hostelRepository.save(hostel));
    }

    @Transactional(readOnly = true)
    public List<HostelDto> getHostels() {
        return hostelRepository.findAllByActiveFlagOrderByHostelNameAsc(Constants.ACTIVE)
                .stream()
                .map(this::toHostelDto)
                .toList();
    }

    @Transactional
    public HostelRoomDto createRoom(HostelRoomDto dto) {
        Hostel hostel = hostelRepository.findByHostelIdAndActiveFlag(dto.getHostelId(), Constants.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found."));

        HostelRoom room = new HostelRoom();
        room.setHostel(hostel);
        room.setRoomNumber(dto.getRoomNumber().trim());
        room.setFloorNumber(dto.getFloorNumber());
        room.setRoomType(dto.getRoomType().trim());
        room.setCapacity(dto.getCapacity());
        room.setOccupiedCount(0);

        return toRoomDto(hostelRoomRepository.save(room));
    }

    @Transactional(readOnly = true)
    public List<HostelRoomDto> getRooms(Long hostelId) {
        List<HostelRoom> rooms = hostelId == null
                ? hostelRoomRepository.findAllByActiveFlagOrderByHostelHostelNameAscRoomNumberAsc(Constants.ACTIVE)
                : hostelRoomRepository.findAllByHostelHostelIdAndActiveFlagOrderByRoomNumberAsc(hostelId, Constants.ACTIVE);

        return rooms.stream().map(this::toRoomDto).toList();
    }

    private void approveRequest(AccommodationRequests request, RequestDecisionDto decisionDto) {
        if (STATUS_APPROVED.equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Request is already approved.");
        }

        if (decisionDto.getRoomId() == null) {
            throw new RuntimeException("Room is required for approval.");
        }

        HostelRoom room = hostelRoomRepository.findByRoomIdAndActiveFlag(decisionDto.getRoomId(), Constants.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found."));

        int requestedBeds = request.getNoOfPersons() == null ? 1 : request.getNoOfPersons();
        int occupiedCount = room.getOccupiedCount() == null ? 0 : room.getOccupiedCount();
        int availableBeds = room.getCapacity() - occupiedCount;

        if (availableBeds < requestedBeds) {
            throw new RuntimeException("Selected room does not have enough available beds.");
        }

        RoomAllocation allocation = new RoomAllocation();
        allocation.setRequest(request);
        allocation.setHostel(room.getHostel());
        allocation.setRoom(room);
        allocation.setUserId(request.getUserId());
        allocation.setUserRole(request.getUserRole());
        allocation.setAllocatedFrom(request.getFromDate());
        allocation.setAllocatedTo(request.getToDate());
        allocation.setBedNumber(blankToNull(decisionDto.getBedNumber()));
        allocation.setAllocationStatus(ALLOCATION_ACTIVE);
        allocation.setAllocationNote(blankToNull(decisionDto.getDecisionNote()));

        room.setOccupiedCount(occupiedCount + requestedBeds);
        updateDecisionFields(request, STATUS_APPROVED, decisionDto.getDecisionNote());

        hostelRoomRepository.save(room);
        accommodationRequestsRepository.save(request);
        roomAllocationRepository.save(allocation);
    }

    private void rejectRequest(AccommodationRequests accommodationRequests, RequestDecisionDto decisionDto) {
        if (STATUS_APPROVED.equalsIgnoreCase(accommodationRequests.getStatus())) {
            throw new RuntimeException("Approved request cannot be rejected after allocation.");
        }

        updateDecisionFields(accommodationRequests, STATUS_REJECTED, decisionDto.getDecisionNote());
        accommodationRequestsRepository.save(accommodationRequests);
    }

    private void updateDecisionFields(AccommodationRequests accommodationRequests, String status, String decisionNote) {
        accommodationRequests.setStatus(status);
        accommodationRequests.setDecisionNote(blankToNull(decisionNote));
        accommodationRequests.setDecidedAt(LocalDateTime.now());
        accommodationRequests.setDecidedBy(SecurityContextUtil.getUsername());
        accommodationRequests.setModifiedAt(LocalDateTime.now());
        accommodationRequests.setModifiedBy(SecurityContextUtil.getUsername());
    }

    private String normalizeDecisionStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new RuntimeException("Decision status is required.");
        }

        String normalized = status.trim().toUpperCase(Locale.ROOT);

        if ("APPROVED".equals(normalized) || "APPROVE".equals(normalized)) {
            return STATUS_APPROVED;
        }

        if ("REJECTED".equals(normalized) || "REJECT".equals(normalized)) {
            return STATUS_REJECTED;
        }

        return status.trim();
    }

    private AccommodationRequestDto toRequestDto(AccommodationRequests request) {
        AccommodationRequestDto dto = accommodationRequestMapper.toDto(request);
        RequesterProfileDto profile = requesterProfileService.resolve(request.getUserId(), request.getUserRole());

        dto.setRequesterCode(profile.requesterCode());
        dto.setRequesterName(profile.requesterName());

        roomAllocationRepository.findByRequestRequestIdAndAllocationStatus(request.getRequestId(), ALLOCATION_ACTIVE)
                .ifPresent(allocation -> {
                    dto.setAllocationId(allocation.getAllocationId());
                    dto.setHostelId(allocation.getHostel().getHostelId());
                    dto.setHostelName(allocation.getHostel().getHostelName());
                    dto.setRoomId(allocation.getRoom().getRoomId());
                    dto.setRoomNumber(allocation.getRoom().getRoomNumber());
                    dto.setBedNumber(allocation.getBedNumber());
                    dto.setAllocationStatus(allocation.getAllocationStatus());
                });

        return dto;
    }

    private HostelDto toHostelDto(Hostel hostel) {
        HostelDto dto = new HostelDto();
        dto.setHostelId(hostel.getHostelId());
        dto.setHostelCode(hostel.getHostelCode());
        dto.setHostelName(hostel.getHostelName());
        dto.setHostelType(hostel.getHostelType());
        dto.setAddress(hostel.getAddress());
        return dto;
    }

    private HostelRoomDto toRoomDto(HostelRoom room) {
        HostelRoomDto dto = new HostelRoomDto();
        dto.setRoomId(room.getRoomId());
        dto.setHostelId(room.getHostel().getHostelId());
        dto.setHostelName(room.getHostel().getHostelName());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setFloorNumber(room.getFloorNumber());
        dto.setRoomType(room.getRoomType());
        dto.setCapacity(room.getCapacity());
        dto.setOccupiedCount(room.getOccupiedCount());
        dto.setAvailableBeds(room.getCapacity() - (room.getOccupiedCount() == null ? 0 : room.getOccupiedCount()));
        return dto;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
