package com.hostel.management.modal;

import com.hostel.management.util.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles", schema = Constants.HOSTEL_SCHEMA)
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "hostel.roles_role_id_seq")
    private Long roleId;

    private String roleName;

    private String description;
}
