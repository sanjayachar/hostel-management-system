package com.hostel.chatservice.repository;

import com.hostel.chatservice.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    boolean existsByRoomRoomIdAndUserId(Long roomId, Long userId);

    Optional<ChatRoomMember> findByRoomRoomIdAndUserId(Long roomId, Long userId);

    List<ChatRoomMember> findByRoomRoomIdOrderByJoinedAtAsc(Long roomId);

    List<ChatRoomMember> findByRoomRoomIdAndUserIdIn(Long roomId, Collection<Long> userIds);

    List<ChatRoomMember> findByUserIdOrderByJoinedAtDesc(Long userId);
}
