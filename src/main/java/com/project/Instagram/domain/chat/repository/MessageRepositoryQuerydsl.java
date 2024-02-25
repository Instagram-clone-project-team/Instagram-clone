package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepositoryQuerydsl {

    Page<Message> findAllByChatRoom(ChatRoom chatRoom, Pageable pageable);
}

