package com.candidate.othercandidateservice.config;

import com.candidate.othercandidateservice.common.audit.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {

    @Bean
    public AuditAwareImpl auditorProvider(){
        return new AuditAwareImpl();
    }
}
