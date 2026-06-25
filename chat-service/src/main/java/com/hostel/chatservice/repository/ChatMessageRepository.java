package com.hostel.chatservice.repository;

import com.hostel.chatservice.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByRoomRoomId(Long roomId, Pageable pageable);

    @Modifying(flushAutomatically = true)
    @Query(
            value = "UPDATE hostel.chat_messages SET room_id = :targetRoomId WHERE room_id = :sourceRoomId",
            nativeQuery = true
    )
    int moveMessagesToRoom(@Param("sourceRoomId") Long sourceRoomId, @Param("targetRoomId") Long targetRoomId);
}
