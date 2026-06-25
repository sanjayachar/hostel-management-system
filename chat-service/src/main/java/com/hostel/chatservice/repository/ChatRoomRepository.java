package com.hostel.chatservice.repository;

import com.hostel.chatservice.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findFirstByRoomTypeOrderByRoomIdAsc(String roomType);

    List<ChatRoom> findByRoomTypeOrderByRoomIdAsc(String roomType);
}
