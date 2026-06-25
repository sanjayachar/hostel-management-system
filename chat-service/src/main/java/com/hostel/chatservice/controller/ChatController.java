package com.hostel.chatservice.controller;

import com.hostel.chatservice.dto.ChatMessageDto;
import com.hostel.chatservice.dto.ChatTypingDto;
import com.hostel.chatservice.dto.ChatTypingRequest;
import com.hostel.chatservice.dto.ChatUser;
import com.hostel.chatservice.dto.SendChatMessageRequest;
import com.hostel.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/rooms/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload SendChatMessageRequest request,
            Principal principal
    ) {
        ChatMessageDto savedMessage = chatService.sendMessage(roomId, request, ChatUser.fromPrincipal(principal));
        log.info("CHAT MESSAGE SAVED -> requestedRoomId: {}, savedRoomId: {}, sender: {}",
                roomId, savedMessage.roomId(), savedMessage.senderUsername());
        simpMessagingTemplate.convertAndSend("/topic/chat/rooms/" + savedMessage.roomId(), savedMessage);

        if (!savedMessage.roomId().equals(roomId)) {
            simpMessagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId, savedMessage);
        }
    }

    @MessageMapping("/chat/rooms/{roomId}/typing")
    public void typing(
            @DestinationVariable Long roomId,
            @Payload ChatTypingRequest request,
            Principal principal
    ) {
        ChatTypingDto typingEvent = chatService.createTypingEvent(roomId, request, ChatUser.fromPrincipal(principal));
        simpMessagingTemplate.convertAndSend("/topic/chat/rooms/" + typingEvent.roomId() + "/typing", typingEvent);

        if (!typingEvent.roomId().equals(roomId)) {
            simpMessagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId + "/typing", typingEvent);
        }
    }
}
