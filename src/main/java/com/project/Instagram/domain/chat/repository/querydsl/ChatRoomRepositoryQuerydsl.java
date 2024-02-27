package com.project.Instagram.domain.chat.repository.querydsl;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChatRoomRepositoryQuerydsl {

    Optional<ChatRoom> findByMemberIdAndRoomId(Long memberId, Long roomId);

    Page<ChatRoom> findByMemberId(Long memberId, Pageable pageable);
}

