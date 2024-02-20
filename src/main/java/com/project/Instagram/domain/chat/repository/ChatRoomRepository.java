package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByChatroomIdAndEmail(Long chatroomId, String email);
}
