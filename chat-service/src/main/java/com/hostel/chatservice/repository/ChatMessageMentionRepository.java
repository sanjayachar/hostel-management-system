package com.hostel.chatservice.repository;

import com.hostel.chatservice.entity.ChatMessageMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageMentionRepository extends JpaRepository<ChatMessageMention, Long> {

    List<ChatMessageMention> findByMessageMessageIdOrderByCreatedAtAsc(Long messageId);

    @Modifying(flushAutomatically = true)
    @Query(
            value = "UPDATE hostel.chat_message_mentions SET room_id = :targetRoomId WHERE room_id = :sourceRoomId",
            nativeQuery = true
    )
    int moveMentionsToRoom(@Param("sourceRoomId") Long sourceRoomId, @Param("targetRoomId") Long targetRoomId);
}
