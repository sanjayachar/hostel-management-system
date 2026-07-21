package com.email.emailservice.controller;

import com.email.emailservice.dto.EmailNotificationEvent;
import com.email.emailservice.entity.EmailNotification;
import com.email.emailservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email/notifications")
public class EmailNotificationController {

    private final EmailNotificationService emailNotificationService;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody EmailNotificationEvent event) {
        EmailNotification notification = emailNotificationService.save(event);
        return ResponseEntity.ok(notification == null ? "Email notification already exists." : notification);
    }
}
