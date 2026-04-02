package com.hostel.management.service;

import com.hostel.management.dto.AccommodationRequestDto;
import com.hostel.management.enums.RoleEnum;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.AccommodationRequestMapper;
import com.hostel.management.modal.*;
import com.hostel.management.repository.AccommodationRequestsRepository;
import com.hostel.management.util.Constants;
import com.hostel.management.util.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRequestsRepository accommodationRequestsRepository;
    private final AccommodationRequestMapper accommodationRequestMapper;

    @Transactional
    public void saveOrUpdateAccommodation(@Valid AccommodationRequestDto accommodationRequestDto) {
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
        User user = SecurityContextUtil.getUser();
        String role = SecurityContextUtil.getRole();
        if (user == null || role == null) {
            throw new ResourceNotFoundException("User or Role not found");
        }
        request.setUser(user);
        request.setUserRole(RoleEnum.valueOf(role));
        accommodationRequestsRepository.save(request);
    }

    public List<AccommodationRequestDto> getRequetList(String userRole) {
        RoleEnum roleEnum = RoleEnum.valueOf(userRole);
        List<AccommodationRequestDto> accommodationRequestDtoList = accommodationRequestsRepository.findAllByUserRoleAndActiveFlag(roleEnum, Constants.ACTIVE)
                .orElse(Collections.emptyList())
                .stream()
                .map(accommodationRequestMapper::toDto)
                .toList();
        return accommodationRequestDtoList;
    }
}
