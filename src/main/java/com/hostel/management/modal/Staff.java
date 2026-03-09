package com.hostel.management.modal;

import com.hostel.management.util.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "staff", schema = Constants.HOSTEL_SCHEMA)
@Getter
@Setter
public class Staff extends CommonEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staff_seq")
    @SequenceGenerator(name = "staff_seq", sequenceName = "hostel.staff_staff_id_seq", allocationSize = 1)
    @Column(name = "staff_id")
    private Long staffId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String gender;

    private LocalDate dateOfBirth;

    private String contactNumber;

    private String email;

    private String address;

    private String designation;

    private String department;

    private LocalDate dateOfJoining;
}
