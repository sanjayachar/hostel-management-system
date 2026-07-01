package com.hostel.chatservice.controller;

import com.hostel.chatservice.dto.ChatMessageDto;
import com.hostel.chatservice.dto.ChatRoomMemberDto;
import com.hostel.chatservice.dto.ChatRoomDto;
import com.hostel.chatservice.dto.ChatUser;
import com.hostel.chatservice.dto.CreateChatRoomRequest;
import com.hostel.chatservice.dto.SendChatMessageRequest;
import com.hostel.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRestController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getRooms(Principal principal) {
        return ResponseEntity.ok(chatService.getRooms(ChatUser.fromPrincipal(principal)));
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDto> createRoom(
            @RequestBody CreateChatRoomRequest request,
            Principal principal
    ) {
        return ResponseEntity.ok(chatService.createRoom(request, ChatUser.fromPrincipal(principal)));
    }

    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<ChatRoomDto> joinRoom(
            @PathVariable Long roomId,
            Principal principal
    ) {
        return ResponseEntity.ok(chatService.joinRoom(roomId, ChatUser.fromPrincipal(principal)));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageDto>> getMessages(
            @PathVariable Long roomId,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
            Principal principal
    ) {
        return ResponseEntity.ok(chatService.getMessages(roomId, ChatUser.fromPrincipal(principal), pageable));
    }

    @GetMapping("/rooms/{roomId}/members")
    public ResponseEntity<List<ChatRoomMemberDto>> getRoomMembers(
            @PathVariable Long roomId,
            Principal principal
    ) {
        return ResponseEntity.ok(chatService.getRoomMembers(roomId, ChatUser.fromPrincipal(principal)));
    }

    @GetMapping("/rooms/{roomId}/mention-users")
    public ResponseEntity<List<ChatRoomMemberDto>> getMentionableUsers(
            @PathVariable Long roomId,
            Principal principal
    ) {
        return ResponseEntity.ok(chatService.getMentionableUsers(roomId, ChatUser.fromPrincipal(principal)));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long roomId,
            @RequestBody SendChatMessageRequest request,
            Principal principal
    ) {
        ChatMessageDto savedMessage = chatService.sendMessage(roomId, request, ChatUser.fromPrincipal(principal));
        broadcastMessage(roomId, savedMessage);
        return ResponseEntity.ok(savedMessage);
    }

    private void broadcastMessage(Long requestedRoomId, ChatMessageDto savedMessage) {
        simpMessagingTemplate.convertAndSend("/topic/chat/rooms/" + savedMessage.roomId(), savedMessage);

        if (!savedMessage.roomId().equals(requestedRoomId)) {
            simpMessagingTemplate.convertAndSend("/topic/chat/rooms/" + requestedRoomId, savedMessage);
        }
    }
}
