package com.hostel.chatservice.repository;

import com.hostel.chatservice.entity.ChatMessageMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageMentionRepository extends JpaRepository<ChatMessageMention, Long> {

    List<ChatMessageMention> findByMessageMessageIdOrderByCreatedAtAsc(Long messageId);

    List<ChatMessageMention> findAllByRoomRoomId(Long roomId);
}
