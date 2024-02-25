package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.entity.Room;
import com.project.Instagram.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryJdbc {
    List<ChatRoom> findByRoomAndMemberIn(Room room, List<Member> members);
    Optional<ChatRoom> findByMemberIdAndRoomId(Long memberId, Long roomId);
    List<ChatRoom> findByRoomId(Long id);
}
