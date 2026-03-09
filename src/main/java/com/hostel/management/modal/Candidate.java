package com.hostel.management.modal;

import com.hostel.management.util.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates", schema = Constants.HOSTEL_SCHEMA)
@Getter
@Setter
public class Candidate extends CommonEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "candidate_seq")
    @SequenceGenerator(name = "candidate_seq", sequenceName = "hostel.candidates_candidate_id_seq", allocationSize = 1)
    @Column(name = "candidate_id")
    private Long candidateId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    private String lastName;

    private String gender;

    private LocalDate dateOfBirth;

    private String email;

    private String contactNumber;

    private String address;

    private String city;

    private String state;

    private String pinCode;

    private String appliedPost;
}
