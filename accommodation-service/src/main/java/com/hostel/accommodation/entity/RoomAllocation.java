package com.hostel.accommodation.entity;

import com.hostel.accommodation.common.entity.CommonEntity;
import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.common.util.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "room_allocations",
        schema = Constants.HOSTEL_SCHEMA,
        uniqueConstraints = @UniqueConstraint(name = "uk_room_allocation_request", columnNames = {"request_id"})
)
@Getter
@Setter
public class RoomAllocation extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allocation_id")
    private Long allocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private AccommodationRequests request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private HostelRoom room;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 40)
    private RoleEnum userRole;

    @Column(name = "allocated_from", nullable = false)
    private LocalDate allocatedFrom;

    @Column(name = "allocated_to", nullable = false)
    private LocalDate allocatedTo;

    @Column(name = "bed_number", length = 40)
    private String bedNumber;

    @Column(name = "allocation_status", nullable = false, length = 30)
    private String allocationStatus = "ACTIVE";

    @Column(name = "allocation_note", length = 500)
    private String allocationNote;
}
