package com.hostel.staff.service;

import com.hostel.staff.client.grpc.AccommodationClient;
import com.hostel.staff.client.grpc.AuthClient;
import com.hostel.staff.client.grpc.StudentDetailsClient;
import com.hostel.staff.common.enums.RoleEnum;
import com.hostel.staff.common.exception.ResourceNotFoundException;
import com.hostel.staff.common.util.Constants;
import com.hostel.staff.common.util.SecurityContextUtil;
import com.hostel.staff.dto.AccommodationRequestDto;
import com.hostel.staff.dto.StaffDto;
import com.hostel.staff.dto.StudentsDto;
import com.hostel.staff.entity.Staff;
import com.hostel.staff.mapper.StaffMapper;
import com.hostel.staff.repository.StaffRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {
    private final StaffMapper staffMapper;
    private final StaffRepo staffRepo;
    private final AuthClient authClient;
    private final AccommodationClient accommodationClient;
    private final StudentDetailsClient studentDetailsClient;

    public void saveOrUpdateStaff(@Valid StaffDto staffDto) {
        if (staffDto.getStaffId()==null) {
            registerStaff(staffDto);
        } else {
            updateStaff(staffDto);
        }
    }
    public void registerStaff(@Valid StaffDto staffDto) {
        Long userId = null;
        try {
            userId = saveUserDetails(staffDto);
            // INTENTIONAL FAILURE
            throw new RuntimeException("Testing Saga Rollback");
            /*Staff staff = staffMapper.toEntityForSave(staffDto);
            staff.setUserId(userId);
            staffRepo.save(staff);*/
        } catch (Exception e) {
            if (userId != null) {
                try {
                    authClient.deleteUser(userId);
                    log.info("Rollback success for user {}", userId);
                } catch (Exception rollbackEx) {
                    log.error("Rollback failed for user {}", userId, rollbackEx);
                }
            }
            throw new RuntimeException("Staff registration failed", e);
        }
        /* future mail service */
    }

    public void updateStaff(@Valid StaffDto staffDto) {
        Staff staff = staffRepo.findById(staffDto.getStaffId()).orElseThrow(()->new RuntimeException("Student Now Found"));
        staff.setFirstName(staffDto.getFirstName());
        staff.setLastName(staffDto.getLastName());
        staff.setGender(staffDto.getGender());
        staff.setDateOfBirth(staffDto.getDateOfBirth());
        staff.setContactNumber(staffDto.getContactNumber());
        staff.setEmail(staffDto.getEmail());
        staff.setAddress(staffDto.getAddress());
        staff.setDesignation(staffDto.getDesignation());
        staff.setDepartment(staffDto.getDepartment());
        staff.setDateOfJoining(staffDto.getDateOfJoining());
        staffRepo.save(staff);
    }

    private Long saveUserDetails(@Valid StaffDto staffDto) {
        String rawPassword = generatePassword();
        log.info("The new raw password for user {} is {}",staffDto.getEmployeeCode(), rawPassword);
        Long userId = authClient.createUser(
                staffDto.getEmployeeCode(),
                rawPassword,
                RoleEnum.ROLE_STAFF.name()
        );
        return userId;
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public List<StaffDto> getAllStaffs() {
        return staffRepo.findAll().stream().map(staffMapper::toDto).collect(Collectors.toList());
    }

    public StaffDto getStaffById(Long staffId) {
        StaffDto staffDto = staffRepo.findByStaffIdAndActiveFlag(staffId, Constants.ACTIVE).map(staffMapper::toDto).orElseThrow(()->new ResourceNotFoundException("Staff not found."));
        return staffDto;
    }

    StaffDto getStaffByEmployeeCode(String username) {
        return staffRepo.findByEmployeeCodeAndActiveFlag(username, Constants.ACTIVE).map(staffMapper::toDto).orElseThrow(()->new ResourceNotFoundException("Staff Not Found"));
    }

    public List<AccommodationRequestDto> getStaffsAccommodationRequest(RoleEnum roleEnum) {
        return accommodationClient.getRequests(roleEnum.name());
    }

    public void saveOrUpdateAccommodation(@Valid AccommodationRequestDto accommodationRequestDto) {
        String token = SecurityContextUtil.getToken();
        Long userId = SecurityContextUtil.getUserId();
        String userRole = SecurityContextUtil.getRole();
        accommodationRequestDto.setUserId(userId);
        accommodationRequestDto.setUserRole(RoleEnum.valueOf(userRole));
        ResponseEntity.ok(accommodationClient.saveRequest(accommodationRequestDto, token));
    }
}
