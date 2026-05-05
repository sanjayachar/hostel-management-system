package com.hostel.accommodation.entity;


import com.hostel.accommodation.common.entity.CommonEntity;
import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.common.util.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "requests", schema = Constants.HOSTEL_SCHEMA)
@Getter
@Setter
public class AccommodationRequests extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_seq")
    @SequenceGenerator(name = "request_seq", sequenceName = "hostel.requests_request_id_seq", allocationSize = 1)
    @Column(name = "request_id")
    private Long requestId;

    private String requestType;
    private String reason;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer noOfDays;
    private Integer noOfPersons;
    private String status;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private RoleEnum userRole;
}
