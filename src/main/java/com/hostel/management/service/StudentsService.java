package com.hostel.management.service;

import com.hostel.management.dto.AccommodationRequestDto;
import com.hostel.management.dto.StudentsDto;
import com.hostel.management.enums.RoleEnum;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.AccommodationRequestMapper;
import com.hostel.management.mapper.StudentMapper;
import com.hostel.management.modal.AccommodationRequests;
import com.hostel.management.modal.Role;
import com.hostel.management.modal.Students;
import com.hostel.management.modal.User;
import com.hostel.management.repository.AccommodationRequestsRepository;
import com.hostel.management.repository.RoleRepository;
import com.hostel.management.repository.StudentRepo;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.util.Constants;
import com.hostel.management.util.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentsService {
    private final AccommodationRequestMapper accommodationRequestMapper;
    private final AccommodationRequestsRepository accommodationRequestsRepository;

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StudentRepo studentRepo;
    private final StudentMapper studentMapper;

    @Transactional
    public void saveOrUpdateStudent(StudentsDto studentsDto) {
        if (studentsDto.getStudentId()==null) {
            registerStudent(studentsDto);
        } else {
            updateStudent(studentsDto);
        }
    }
    public void registerStudent(StudentsDto studentsDto) {
        User user = saveUserDetails(studentsDto);
        Students students = studentMapper.toEntityForSave(studentsDto);
        students.setUser(user);
        studentRepo.save(students);
        /* future mail service */
    }

    public void updateStudent(StudentsDto studentsDto) {
        Students students = studentRepo.findById(studentsDto.getStudentId()).orElseThrow(()->new RuntimeException("Student Now Found"));
        students.setFirstName(studentsDto.getFirstName());
        students.setLastName(studentsDto.getLastName());
        students.setGender(studentsDto.getGender());
        students.setDateOfBirth(studentsDto.getDateOfBirth());
        students.setContactNumber(studentsDto.getContactNumber());
        students.setPersonalEmail(studentsDto.getPersonalEmail());
        students.setFatherName(studentsDto.getFatherName());
        students.setMotherName(studentsDto.getMotherName());
        students.setAddress(studentsDto.getAddress());
        students.setHostelStatus(studentsDto.getHostelStatus());
        studentRepo.save(students);
    }

    private User saveUserDetails(StudentsDto studentsDto) {
        User user = new User();
        String rawPassword = generatePassword();
        log.info("The new raw password for user {} is {}",studentsDto.getAdmissionNumber(), rawPassword);
        Role role = roleRepository.findByRoleName(RoleEnum.ROLE_STUDENT).orElseThrow(()->new RuntimeException("Role not found"));
        user.setUsername(studentsDto.getAdmissionNumber());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public List<StudentsDto> getAllStudents() {
        return studentRepo.findAllByActiveFlag(Constants.ACTIVE).orElse(Collections.emptyList()).stream().map(studentMapper::toDto).collect(Collectors.toList());
    }

    public StudentsDto getStudentById(Long studentId) {
        StudentsDto studentsDto = studentRepo.findByStudentIdAndActiveFlag(studentId, Constants.ACTIVE).map(studentMapper::toDto).orElseThrow(()->new ResourceNotFoundException("Student Not Found."));
        return studentsDto;
    }

    public StudentsDto getStudentByAdmissionNo(String admissionNo) {
        StudentsDto studentsDto = studentRepo.findByAdmissionNumberAndActiveFlag(admissionNo, Constants.ACTIVE).map(studentMapper::toDto).orElseThrow(()->new ResourceNotFoundException("Student Not Found."));
        return studentsDto;
    }
}
