package com.hostel.chatservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDto {
    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime timestamp;
}
