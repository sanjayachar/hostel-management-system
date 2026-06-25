package com.hostel.accommodation.entity;

import com.hostel.accommodation.common.entity.CommonEntity;
import com.hostel.accommodation.common.util.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(
        name = "hostel_rooms",
        schema = Constants.HOSTEL_SCHEMA,
        uniqueConstraints = @UniqueConstraint(name = "uk_hostel_room_number", columnNames = {"hostel_id", "room_number"})
)
@Getter
@Setter
public class HostelRoom extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    @Column(name = "room_number", nullable = false, length = 40)
    private String roomNumber;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "room_type", nullable = false, length = 60)
    private String roomType;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "occupied_count", nullable = false)
    private Integer occupiedCount = 0;
}
