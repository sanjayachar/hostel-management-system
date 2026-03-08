package com.hostel.management.modal;

import com.hostel.management.util.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", schema = Constants.HOSTEL_SCHEMA)
@Getter
@Setter
public class User extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "hostel.users_user_id_seq")
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "account_non_locked")
    private Boolean accountNonLocked = true;

    @Column(name = "account_non_expired")
    private Boolean accountNonExpired = true;

    @Column(name = "credentials_non_expired")
    private Boolean credentialsNonExpired = true;

    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;

    @Column(name = "token_version")
    private Integer tokenVersion = 0;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
}
