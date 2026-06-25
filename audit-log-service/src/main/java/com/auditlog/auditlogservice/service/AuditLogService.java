package com.auditlog.auditlogservice.service;

import com.auditlog.auditlogservice.dto.AuditLogEvent;
import com.auditlog.auditlogservice.entity.AuditLog;
import com.auditlog.auditlogservice.repo.AuditLogRepo;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepo auditLogRepository;

    @Transactional
    public void save(AuditLogEvent event) {
        if (event.eventId() != null && auditLogRepository.existsByEventId(event.eventId())) {
            return;
        }

        AuditLog log = new AuditLog();

        log.setEventId(event.eventId());
        log.setEventTime(event.eventTime());
        log.setServiceName(event.serviceName());
        log.setLevel(event.level());
        log.setActorUserId(event.actorUserId());
        log.setActorUsername(event.actorUsername());
        log.setActorRole(event.actorRole());
        log.setClassName(event.className());
        log.setMethodName(event.methodName());
        log.setActionName(event.actionName());
        log.setRequestPath(event.requestPath());
        log.setHttpMethod(event.httpMethod());
        log.setStatus(event.status());
        log.setMessage(event.message());
        log.setErrorMessage(event.errorMessage());
        log.setDurationMs(event.durationMs());

        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> search(
            String serviceName,
            String level,
            String status,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            String search,
            Pageable pageable
    ) {
        Specification<AuditLog> specification = buildSpecification(
                blankToNull(serviceName),
                blankToNull(level),
                blankToNull(status),
                fromTime,
                toTime,
                blankToNull(search)
        );

        return auditLogRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public AuditLog getById(Long auditLogId) {
        return auditLogRepository.findById(auditLogId)
                .orElseThrow(() -> new RuntimeException("Audit log not found"));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private Specification<AuditLog> buildSpecification(
            String serviceName,
            String level,
            String status,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            String search
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (serviceName != null) {
                predicates.add(criteriaBuilder.equal(root.get("serviceName"), serviceName));
            }

            if (level != null) {
                predicates.add(criteriaBuilder.equal(root.get("level"), level));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (fromTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventTime"), fromTime));
            }

            if (toTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventTime"), toTime));
            }

            if (search != null) {
                String searchPattern = "%" + search.toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("message")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("errorMessage")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("className")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("methodName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("actorUsername")), searchPattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
