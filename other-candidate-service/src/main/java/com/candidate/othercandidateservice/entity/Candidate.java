package com.candidate.othercandidateservice.entity;

import com.candidate.othercandidateservice.common.entity.CommonEntity;
import com.candidate.othercandidateservice.common.util.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "candidates", schema = Constants.HOSTEL_SCHEMA)
@Getter
@Setter
public class Candidate extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "candidate_seq")
    @SequenceGenerator(name = "candidate_seq", sequenceName = "hostel.candidates_candidate_id_seq", allocationSize = 1)
    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "candidate_code")
    private String candidateCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email")
    private String email;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pin_code")
    private String pinCode;

    @Column(name = "applied_post")
    private String appliedPost;
}
