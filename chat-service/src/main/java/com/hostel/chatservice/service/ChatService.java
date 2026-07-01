package com.hostel.chatservice.service;

import com.hostel.chatservice.dto.ChatMessageDto;
import com.hostel.chatservice.dto.ChatMentionDto;
import com.hostel.chatservice.dto.ChatRoomMemberDto;
import com.hostel.chatservice.dto.ChatRoomDto;
import com.hostel.chatservice.dto.ChatTypingDto;
import com.hostel.chatservice.dto.ChatTypingRequest;
import com.hostel.chatservice.dto.ChatUser;
import com.hostel.chatservice.dto.ChatUserProfileDto;
import com.hostel.chatservice.dto.CreateChatRoomRequest;
import com.hostel.chatservice.dto.EmailNotificationEvent;
import com.hostel.chatservice.dto.SendChatMessageRequest;
import com.hostel.chatservice.entity.ChatMessage;
import com.hostel.chatservice.entity.ChatMessageMention;
import com.hostel.chatservice.entity.ChatRoom;
import com.hostel.chatservice.entity.ChatRoomMember;
import com.hostel.chatservice.repository.ChatMessageMentionRepository;
import com.hostel.chatservice.repository.ChatMessageRepository;
import com.hostel.chatservice.repository.ChatRoomMemberRepository;
import com.hostel.chatservice.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String HELP_DESK_ROOM_TYPE = "HELP_DESK";
    private static final String GROUP_ROOM_TYPE = "GROUP";

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMentionRepository chatMessageMentionRepository;
    private final ChatUserDisplayNameService chatUserDisplayNameService;
    private final ChatUserProfileService chatUserProfileService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${hostel.email.admin-recipients:}")
    private String adminRecipients;

    @Transactional
    public List<ChatRoomDto> getRooms(ChatUser user) {
        ChatRoom defaultRoom = ensureHelpDeskRoom();
        ensureMember(defaultRoom, user);

        Map<Long, ChatRoomDto> rooms = new LinkedHashMap<>();
        rooms.put(defaultRoom.getRoomId(), toRoomDto(defaultRoom));
        chatRoomMemberRepository.findByUserIdOrderByJoinedAtDesc(user.userId())
                .forEach(member -> rooms.putIfAbsent(member.getRoom().getRoomId(), toRoomDto(member.getRoom())));

        return List.copyOf(rooms.values());
    }

    @Transactional
    public ChatRoomDto createRoom(CreateChatRoomRequest request, ChatUser user) {
        String roomName = request.roomName() == null || request.roomName().isBlank()
                ? "New Group"
                : request.roomName().trim();
        String roomType = request.roomType() == null || request.roomType().isBlank()
                ? GROUP_ROOM_TYPE
                : request.roomType().trim().toUpperCase();

        if (HELP_DESK_ROOM_TYPE.equals(roomType)) {
            ChatRoom room = ensureHelpDeskRoom();
            ensureMember(room, user);
            return toRoomDto(room);
        }

        ChatRoom room = new ChatRoom();
        room.setRoomName(roomName);
        room.setRoomType(roomType);
        room.setCreatedBy(user.userId());

        ChatRoom savedRoom = chatRoomRepository.save(room);
        ensureMember(savedRoom, user);

        return toRoomDto(savedRoom);
    }

    @Transactional
    public ChatRoomDto joinRoom(Long roomId, ChatUser user) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        if (HELP_DESK_ROOM_TYPE.equals(room.getRoomType())) {
            room = ensureHelpDeskRoom();
        }

        ensureMember(room, user);

        return toRoomDto(room);
    }

    @Transactional
    public Page<ChatMessageDto> getMessages(Long roomId, ChatUser user, Pageable pageable) {
        ChatRoom room = requireAccessibleRoom(roomId, user);
        return chatMessageRepository.findByRoomRoomId(room.getRoomId(), pageable).map(this::toMessageDto);
    }

    @Transactional
    public List<ChatRoomMemberDto> getRoomMembers(Long roomId, ChatUser user) {
        ChatRoom room = requireAccessibleRoom(roomId, user);
        return chatRoomMemberRepository.findByRoomRoomIdOrderByJoinedAtAsc(room.getRoomId())
                .stream()
                .map(this::toMemberDto)
                .toList();
    }

    @Transactional
    public List<ChatRoomMemberDto> getMentionableUsers(Long roomId, ChatUser user) {
        ChatRoom room = requireAccessibleRoom(roomId, user);
        Map<Long, ChatRoomMemberDto> users = new LinkedHashMap<>();

        chatUserProfileService.findAllProfiles().stream()
                .filter(profile -> profile.userId() != null)
                .forEach(profile -> users.put(profile.userId(), toMemberDto(profile)));

        chatRoomMemberRepository.findByRoomRoomIdOrderByJoinedAtAsc(room.getRoomId())
                .forEach(member -> users.putIfAbsent(member.getUserId(), toMemberDto(member)));

        return users.values().stream()
                .sorted(Comparator.comparing(member -> defaultValue(member.username(), "").toLowerCase()))
                .toList();
    }

    @Transactional
    public ChatMessageDto sendMessage(Long roomId, SendChatMessageRequest request, ChatUser user) {
        ChatRoom room = requireAccessibleRoom(roomId, user);

        if (request == null || request.message() == null || request.message().isBlank()) {
            throw new RuntimeException("Message cannot be empty");
        }

        List<Long> mentionedUserIds = normalizeMentionedUserIds(request.mentionedUserIds(), user.userId());

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSenderUserId(user.userId());
        message.setSenderUsername(chatUserDisplayNameService.getDisplayName(user));
        message.setSenderRole(user.role());
        message.setMessage(request.message().trim());

        ChatMessage savedMessage = chatMessageRepository.save(message);
        List<ChatMessageMention> mentions = saveMentions(savedMessage, room, mentionedUserIds);
        publishMentionEmails(savedMessage, room, mentions);

        return toMessageDto(savedMessage, mentions);
    }

    @Transactional
    public ChatTypingDto createTypingEvent(Long roomId, ChatTypingRequest request, ChatUser user) {
        ChatRoom room = requireAccessibleRoom(roomId, user);

        return new ChatTypingDto(
                room.getRoomId(),
                user.userId(),
                chatUserDisplayNameService.getDisplayName(user),
                user.role(),
                Boolean.TRUE.equals(request.typing()),
                LocalDateTime.now()
        );
    }

    private ChatRoom requireAccessibleRoom(Long roomId, ChatUser user) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        if (HELP_DESK_ROOM_TYPE.equals(room.getRoomType())) {
            ChatRoom helpDeskRoom = ensureHelpDeskRoom();
            ensureMember(helpDeskRoom, user);
            return helpDeskRoom;
        }

        if (chatRoomMemberRepository.existsByRoomRoomIdAndUserId(roomId, user.userId())) {
            return room;
        }

        throw new AccessDeniedException("You are not a member of this chat room.");
    }

    private ChatRoom ensureHelpDeskRoom() {
        List<ChatRoom> rooms = chatRoomRepository.findByRoomTypeOrderByRoomIdAsc(HELP_DESK_ROOM_TYPE);

        if (rooms.isEmpty()) {
            ChatRoom room = new ChatRoom();
            room.setRoomName("Hostel Help Desk");
            room.setRoomType(HELP_DESK_ROOM_TYPE);
            return chatRoomRepository.save(room);
        }

        ChatRoom primaryRoom = rooms.get(0);

        for (int index = 1; index < rooms.size(); index++) {
            mergeRoomIntoPrimary(primaryRoom, rooms.get(index));
        }

        return primaryRoom;
    }

    private void mergeRoomIntoPrimary(ChatRoom primaryRoom, ChatRoom duplicateRoom) {
        List<ChatMessage> messages = chatMessageRepository.findAllByRoomRoomId(duplicateRoom.getRoomId());
        messages.forEach(message -> message.setRoom(primaryRoom));
        chatMessageRepository.saveAll(messages);

        List<ChatMessageMention> mentions = chatMessageMentionRepository.findAllByRoomRoomId(duplicateRoom.getRoomId());
        mentions.forEach(mention -> mention.setRoom(primaryRoom));
        chatMessageMentionRepository.saveAll(mentions);

        chatRoomMemberRepository.findByRoomRoomIdOrderByJoinedAtAsc(duplicateRoom.getRoomId())
                .forEach(member -> {
                    if (chatRoomMemberRepository.existsByRoomRoomIdAndUserId(primaryRoom.getRoomId(), member.getUserId())) {
                        chatRoomMemberRepository.delete(member);
                        return;
                    }

                    member.setRoom(primaryRoom);
                    chatRoomMemberRepository.save(member);
                });

        chatRoomRepository.delete(duplicateRoom);
    }

    private void ensureMember(ChatRoom room, ChatUser user) {
        String displayName = chatUserDisplayNameService.getDisplayName(user);
        Optional<ChatRoomMember> existingMember = chatRoomMemberRepository.findByRoomRoomIdAndUserId(
                room.getRoomId(), user.userId());

        if (existingMember.isPresent()) {
            ChatRoomMember member = existingMember.get();
            boolean needsUpdate = !displayName.equals(member.getUsername()) || !user.role().equals(member.getUserRole());

            if (needsUpdate) {
                member.setUsername(displayName);
                member.setUserRole(user.role());
                chatRoomMemberRepository.save(member);
            }

            return;
        }

        ChatRoomMember member = new ChatRoomMember();
        member.setRoom(room);
        member.setUserId(user.userId());
        member.setUsername(displayName);
        member.setUserRole(user.role());
        chatRoomMemberRepository.save(member);
    }

    private List<Long> normalizeMentionedUserIds(List<Long> mentionedUserIds, Long senderUserId) {
        if (mentionedUserIds == null || mentionedUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();

        mentionedUserIds.stream()
                .filter(userId -> userId != null && userId > 0)
                .filter(userId -> !userId.equals(senderUserId))
                .forEach(uniqueIds::add);

        return List.copyOf(uniqueIds);
    }

    private List<ChatMessageMention> saveMentions(ChatMessage message, ChatRoom room, List<Long> mentionedUserIds) {
        if (mentionedUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, ChatRoomMember> existingMembersByUserId = chatRoomMemberRepository
                .findByRoomRoomIdAndUserIdIn(room.getRoomId(), mentionedUserIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(ChatRoomMember::getUserId, member -> member));

        List<ChatMessageMention> mentions = new ArrayList<>();
        for (Long userId : mentionedUserIds) {
            ChatRoomMember member = existingMembersByUserId.get(userId);

            if (member == null) {
                ChatUserProfileDto profile = chatUserProfileService.findProfile(userId)
                        .orElseThrow(() -> new RuntimeException("Mentioned user profile not found."));
                member = ensureMember(room, profile);
            }

            ChatMessageMention mention = new ChatMessageMention();
            mention.setMessage(message);
            mention.setRoom(room);
            mention.setMentionedUserId(member.getUserId());
            mention.setMentionedUsername(member.getUsername());
            mention.setMentionedRole(member.getUserRole());
            mention.setReadStatus(false);
            mentions.add(mention);
        }

        return chatMessageMentionRepository.saveAll(mentions);
    }

    private void publishMentionEmails(ChatMessage message, ChatRoom room, List<ChatMessageMention> mentions) {
        if (mentions.isEmpty()) {
            return;
        }

        mentions.forEach(mention -> {
            if ("ROLE_ADMIN".equals(mention.getMentionedRole())) {
                publishAdminMentionEmails(message, room, mention);
                return;
            }

            chatUserProfileService.findProfile(mention.getMentionedUserId(), mention.getMentionedRole())
                    .filter(profile -> !isBlank(profile.email()))
                    .ifPresent(profile -> publishMentionEmail(message, room, mention, profile));
        });
    }

    private void publishMentionEmail(
            ChatMessage message,
            ChatRoom room,
            ChatMessageMention mention,
            ChatUserProfileDto profile
    ) {
        applicationEventPublisher.publishEvent(new EmailNotificationEvent(
                UUID.randomUUID().toString(),
                "CHAT_MENTIONED",
                "CHAT_MENTIONED",
                profile.userId(),
                defaultValue(profile.role(), mention.getMentionedRole()),
                profile.email(),
                defaultValue(profile.displayName(), mention.getMentionedUsername()),
                null,
                "chat-service",
                "CHAT_MESSAGE",
                message.getMessageId(),
                mentionEmailVariables(message, room, mention),
                LocalDateTime.now()
        ));
    }

    private void publishAdminMentionEmails(ChatMessage message, ChatRoom room, ChatMessageMention mention) {
        adminRecipientEmails().forEach(adminEmail ->
                applicationEventPublisher.publishEvent(new EmailNotificationEvent(
                        UUID.randomUUID().toString(),
                        "CHAT_MENTIONED",
                        "CHAT_MENTIONED",
                        mention.getMentionedUserId(),
                        "ROLE_ADMIN",
                        adminEmail,
                        defaultValue(mention.getMentionedUsername(), "Admin"),
                        "ADMIN",
                        "chat-service",
                        "CHAT_MESSAGE",
                        message.getMessageId(),
                        mentionEmailVariables(message, room, mention),
                        LocalDateTime.now()
                ))
        );
    }

    private Map<String, String> mentionEmailVariables(ChatMessage message, ChatRoom room, ChatMessageMention mention) {
        Map<String, String> variables = new LinkedHashMap<>();
        variables.put("roomId", String.valueOf(room.getRoomId()));
        variables.put("roomName", defaultValue(room.getRoomName(), "Chat"));
        variables.put("messageId", String.valueOf(message.getMessageId()));
        variables.put("senderName", defaultValue(message.getSenderUsername(), "Someone"));
        variables.put("senderRole", defaultValue(message.getSenderRole(), "-"));
        variables.put("mentionedName", defaultValue(mention.getMentionedUsername(), "User"));
        variables.put("messagePreview", messagePreview(message.getMessage()));
        return variables;
    }

    private List<String> adminRecipientEmails() {
        if (isBlank(adminRecipients)) {
            return List.of();
        }

        return Arrays.stream(adminRecipients.split(","))
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .distinct()
                .toList();
    }

    private String messagePreview(String message) {
        if (isBlank(message)) {
            return "";
        }

        String normalized = message.trim();
        return normalized.length() <= 180 ? normalized : normalized.substring(0, 180) + "...";
    }

    private ChatRoomDto toRoomDto(ChatRoom room) {
        return new ChatRoomDto(
                room.getRoomId(),
                room.getRoomName(),
                room.getRoomType(),
                room.getCreatedBy(),
                room.getCreatedAt()
        );
    }

    private ChatMessageDto toMessageDto(ChatMessage message) {
        return toMessageDto(
                message,
                chatMessageMentionRepository.findByMessageMessageIdOrderByCreatedAtAsc(message.getMessageId())
        );
    }

    private ChatMessageDto toMessageDto(ChatMessage message, List<ChatMessageMention> mentions) {
        return new ChatMessageDto(
                message.getMessageId(),
                message.getRoom().getRoomId(),
                message.getSenderUserId(),
                chatUserDisplayNameService.resolveDisplayName(
                        message.getSenderUserId(),
                        message.getSenderUsername(),
                        message.getSenderRole()
                ),
                message.getSenderRole(),
                message.getMessage(),
                mentions.stream().map(this::toMentionDto).toList(),
                message.getCreatedAt()
        );
    }

    private ChatMentionDto toMentionDto(ChatMessageMention mention) {
        return new ChatMentionDto(
                mention.getMentionId(),
                mention.getMentionedUserId(),
                mention.getMentionedUsername(),
                mention.getMentionedRole(),
                mention.getReadStatus()
        );
    }

    private ChatRoomMemberDto toMemberDto(ChatRoomMember member) {
        return new ChatRoomMemberDto(
                member.getUserId(),
                chatUserDisplayNameService.resolveDisplayName(
                        member.getUserId(),
                        member.getUsername(),
                        member.getUserRole()
                ),
                member.getUserRole()
        );
    }

    private ChatRoomMemberDto toMemberDto(ChatUserProfileDto profile) {
        return new ChatRoomMemberDto(
                profile.userId(),
                defaultValue(profile.displayName(), "User #" + profile.userId()),
                defaultValue(profile.role(), "ROLE_USER")
        );
    }

    private ChatRoomMember ensureMember(ChatRoom room, ChatUserProfileDto profile) {
        return chatRoomMemberRepository.findByRoomRoomIdAndUserId(room.getRoomId(), profile.userId())
                .map(member -> updateMemberIfNeeded(member, profile))
                .orElseGet(() -> createMember(room, profile));
    }

    private ChatRoomMember updateMemberIfNeeded(ChatRoomMember member, ChatUserProfileDto profile) {
        String displayName = defaultValue(profile.displayName(), member.getUsername());
        String role = defaultValue(profile.role(), member.getUserRole());
        boolean needsUpdate = !displayName.equals(member.getUsername()) || !role.equals(member.getUserRole());

        if (!needsUpdate) {
            return member;
        }

        member.setUsername(displayName);
        member.setUserRole(role);
        return chatRoomMemberRepository.save(member);
    }

    private ChatRoomMember createMember(ChatRoom room, ChatUserProfileDto profile) {
        ChatRoomMember member = new ChatRoomMember();
        member.setRoom(room);
        member.setUserId(profile.userId());
        member.setUsername(defaultValue(profile.displayName(), "User #" + profile.userId()));
        member.setUserRole(defaultValue(profile.role(), "ROLE_USER"));
        return chatRoomMemberRepository.save(member);
    }

    private String defaultValue(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
