package com.hostel.chatservice.repository;

import com.hostel.chatservice.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByRoomRoomId(Long roomId, Pageable pageable);

    List<ChatMessage> findAllByRoomRoomId(Long roomId);
}
