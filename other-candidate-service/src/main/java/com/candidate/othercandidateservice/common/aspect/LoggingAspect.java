package com.candidate.othercandidateservice.common.aspect;

import com.candidate.othercandidateservice.dto.AuditLogEvent;
import com.candidate.othercandidateservice.kafka.AuditLogProducer;
import com.candidate.othercandidateservice.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final String SERVICE_NAME = "other-candidate-service";

    private final AuditLogProducer auditLogProducer;

    @Around("execution(* com.candidate.othercandidateservice.controller..*(..)) || " + "execution(* com.candidate.othercandidateservice.service..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        log.info("METHOD START -> {}.{}", className, methodName);
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("METHOD END -> {}.{} | Time: {} ms", className, methodName, executionTime);
            publish(joinPoint, "INFO", "SUCCESS", "Method executed successfully", null, executionTime);
            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("METHOD ERROR -> {}.{} | Exception: {} | Time: {} ms", className, methodName, ex.getMessage(), executionTime);
            publish(joinPoint, "ERROR", "ERROR", "Method execution failed", ex.getMessage(), executionTime);
            throw ex;
        }
    }

    private void publish(
            ProceedingJoinPoint joinPoint,
            String level,
            String status,
            String message,
            String errorMessage,
            long executionTime
    ) {
        Actor actor = currentActor();
        RequestInfo requestInfo = currentRequest();

        auditLogProducer.publish(new AuditLogEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                SERVICE_NAME,
                level,
                actor.userId(),
                actor.username(),
                actor.role(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getName(),
                requestInfo.path(),
                requestInfo.method(),
                status,
                message,
                errorMessage,
                executionTime
        ));
    }

    private Actor currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return new Actor(null, null, null);
        }

        Long userId = null;
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            userId = userPrincipal.getUserId();
            username = userPrincipal.getUsername();
        }

        return new Actor(userId, username, role);
    }

    private RequestInfo currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            return new RequestInfo(request.getRequestURI(), request.getMethod());
        }

        return new RequestInfo(null, null);
    }

    private record Actor(Long userId, String username, String role) {
    }

    private record RequestInfo(String path, String method) {
    }
}
