package com.hostel.chatservice.controller;

import com.hostel.chatservice.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public void sendMessage(ChatMessageDto message){
        log.info("MESSAGE RECEIVED: {}", message);
        simpMessagingTemplate.convertAndSend("/topic/public", message);
    }
}
