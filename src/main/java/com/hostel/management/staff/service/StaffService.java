package com.hostel.management.staff.service;

import com.hostel.management.auth.entity.Role;
import com.hostel.management.auth.entity.User;
import com.hostel.management.staff.dto.StaffDto;
import com.hostel.management.common.enums.RoleEnum;
import com.hostel.management.common.exception.ResourceNotFoundException;
import com.hostel.management.staff.mapper.StaffMapper;
import com.hostel.management.accommodation.repository.AccommodationRequestsRepository;
import com.hostel.management.auth.repository.RoleRepository;
import com.hostel.management.staff.entity.Staff;
import com.hostel.management.staff.repository.StaffRepo;
import com.hostel.management.auth.repository.UserRepository;
import com.hostel.management.common.util.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {
    private final AccommodationRequestsRepository accommodationRequestsRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StaffMapper staffMapper;
    private final StaffRepo staffRepo;
    private final PasswordEncoder passwordEncoder;

    public void saveOrUpdateStaff(@Valid StaffDto staffDto) {
        if (staffDto.getStaffId()==null) {
            registerStaff(staffDto);
        } else {
            updateStaff(staffDto);
        }
    }
    public void registerStaff(@Valid StaffDto staffDto) {
        User user = saveUserDetails(staffDto);
        Staff staff = staffMapper.toEntityForSave(staffDto);
        staff.setUser(user);
        staffRepo.save(staff);
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

    private User saveUserDetails(@Valid StaffDto staffDto) {
        User user = new User();
        String rawPassword = generatePassword();
        log.info("The new raw password for user {} is {}",staffDto.getEmployeeCode(), rawPassword);
        Role role = roleRepository.findByRoleName(RoleEnum.ROLE_STAFF).orElseThrow(()->new RuntimeException("Role not found"));
        user.setUsername(staffDto.getEmployeeCode());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
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
}
