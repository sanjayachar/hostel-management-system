package com.hostel.accommodation.entity;

import com.hostel.accommodation.common.entity.CommonEntity;
import com.hostel.accommodation.common.util.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hostels", schema = Constants.HOSTEL_SCHEMA)
@Getter
@Setter
public class Hostel extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hostel_id")
    private Long hostelId;

    @Column(name = "hostel_code", nullable = false, unique = true, length = 40)
    private String hostelCode;

    @Column(name = "hostel_name", nullable = false, length = 150)
    private String hostelName;

    @Column(name = "hostel_type", nullable = false, length = 40)
    private String hostelType;

    @Column(name = "address", length = 500)
    private String address;
}
