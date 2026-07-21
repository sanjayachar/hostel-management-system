package com.hostel.student.service;


import com.hostel.student.client.ServiceCall;
import com.hostel.student.client.grpc.AccommodationClient;
import com.hostel.student.client.grpc.AuthClient;
import com.hostel.student.common.enums.RoleEnum;
import com.hostel.student.common.exception.ResourceNotFoundException;
import com.hostel.student.common.util.Constants;
import com.hostel.student.common.util.SecurityContextUtil;
import com.hostel.student.dto.AccommodationRequestDto;
import com.hostel.student.dto.CreatedAuthUser;
import com.hostel.student.dto.EmailNotificationApplicationEvent;
import com.hostel.student.dto.EmailNotificationEvent;
import com.hostel.student.dto.StudentsDto;
import com.hostel.student.entity.Students;
import com.hostel.student.mapper.StudentMapper;
import com.hostel.student.repository.StudentRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentsService {
    private final PasswordEncoder passwordEncoder;
    private final StudentRepo studentRepo;
    private final StudentMapper studentMapper;
    private final ServiceCall serviceCall;
    private final AuthClient authClient;
    private final AccommodationClient accommodationClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void saveOrUpdateStudent(StudentsDto studentsDto) {
        if (studentsDto.getStudentId()==null) {
            registerStudent(studentsDto);
        } else {
            updateStudent(studentsDto);
        }
    }
    public void registerStudent(StudentsDto studentsDto) {
        Long userId = null;
        try {
            CreatedAuthUser createdAuthUser = saveUserDetails(studentsDto);
            Students students = studentMapper.toEntityForSave(studentsDto);
            userId = createdAuthUser.userId();
            students.setUserId(userId);
            studentRepo.save(students);
            EmailNotificationEvent emailEvent = new EmailNotificationEvent(
                    UUID.randomUUID().toString(),
                    "USER_CREATED",
                    "USER_CREATED",
                    userId,
                    "ROLE_STUDENT",
                    studentsDto.getPersonalEmail(),
                    studentsDto.getFirstName() + " " + studentsDto.getLastName(),
                    null,
                    "student-service",
                    "STUDENT",
                    students.getStudentId(),
                    Map.of(
                            "username", studentsDto.getAdmissionNumber(),
                            "temporaryPassword", createdAuthUser.temporaryPassword(),
                            "instruction", "Please login using this temporary password and reset your password immediately."
                    ),
                    LocalDateTime.now()
            );
            applicationEventPublisher.publishEvent(emailEvent);
        } catch (Exception e) {
            if (userId != null) {
                try {
                    authClient.deleteUser(userId);
                    log.info("Rollback success for user {}", userId);
                } catch (Exception rollbackEx) {
                    log.error("Rollback failed for user {}", userId, rollbackEx);
                }
            }
            throw new RuntimeException("Student registration failed", e);
        }
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

    private CreatedAuthUser saveUserDetails(StudentsDto studentsDto) {
        String rawPassword = generatePassword();
        return authClient.createUser(
                studentsDto.getAdmissionNumber(),
                rawPassword,
                RoleEnum.ROLE_STUDENT.name()
        );
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public List<StudentsDto> getAllStudents() {
        return studentRepo.findAllByActiveFlag(Constants.ACTIVE).orElse(Collections.emptyList()).stream().map(studentMapper::toDto).collect(Collectors.toList());
    }

    public List<Students> getActiveStudents() {
        return studentRepo.findAllByActiveFlag(Constants.ACTIVE).orElse(Collections.emptyList());
    }

    public StudentsDto getStudentById(Long studentId) {
        StudentsDto studentsDto = studentRepo.findByStudentIdAndActiveFlag(studentId, Constants.ACTIVE).map(studentMapper::toDto).orElseThrow(()->new ResourceNotFoundException("Student Not Found."));
        return studentsDto;
    }

    public StudentsDto getStudentByAdmissionNo(String admissionNo) {
        StudentsDto studentsDto = studentRepo.findByAdmissionNumberAndActiveFlag(admissionNo, Constants.ACTIVE).map(studentMapper::toDto).orElseThrow(()->new ResourceNotFoundException("Student Not Found."));
        return studentsDto;
    }

    public Optional<Students> getActiveStudentByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        return studentRepo.findFirstByUserIdAndActiveFlagOrderByStudentIdDesc(userId, Constants.ACTIVE);
    }

    public List<AccommodationRequestDto> getStudentsAccommodationRequest(RoleEnum roleEnum) {
        return accommodationClient.getRequests(roleEnum.name(), SecurityContextUtil.getUserId());
    }

    public String getNextAdmissionNumber() {
        String prefix = "STU" + Year.now().getValue();
        int maxSuffix = studentRepo.findAllByAdmissionNumberStartingWith(prefix).stream()
                .map(Students::getAdmissionNumber)
                .mapToInt(admissionNumber -> extractSuffix(admissionNumber, prefix))
                .max()
                .orElse(0);
        int nextSuffix = maxSuffix + 1;
        return prefix + String.format("%03d", nextSuffix);
    }

    private int extractSuffix(String value, String prefix) {
        if (value == null || !value.startsWith(prefix)) {
            return 0;
        }

        String suffix = value.substring(prefix.length());
        if (suffix.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(suffix);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public ResponseEntity<?> saveOrUpdateAccommodation(@Valid AccommodationRequestDto accommodationRequestDto) {
        String token = SecurityContextUtil.getToken();
        Long userId = SecurityContextUtil.getUserId();
        String userRole = SecurityContextUtil.getRole();
        accommodationRequestDto.setUserId(userId);
        accommodationRequestDto.setUserRole(RoleEnum.valueOf(userRole));
        return ResponseEntity.ok(accommodationClient.saveRequest(accommodationRequestDto, token));
    }
}
