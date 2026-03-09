package com.hostel.management.service;

import com.hostel.management.dto.StudentsDto;
import com.hostel.management.enums.RoleEnum;
import com.hostel.management.mapper.StudentMapper;
import com.hostel.management.modal.Role;
import com.hostel.management.modal.Students;
import com.hostel.management.modal.User;
import com.hostel.management.repository.RoleRepository;
import com.hostel.management.repository.StudentRepo;
import com.hostel.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentsService {

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
        Role role = roleRepository.findByRoleName(RoleEnum.ROLE_STUDENT.name()).orElseThrow(()->new RuntimeException("Role not found"));
        user.setUsername(studentsDto.getAdmissionNumber());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
