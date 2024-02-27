package com.project.Instagram.domain.chat.repository.jdbc;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.entity.Message;

import java.util.List;

public interface ChatRoomRepositoryJdbc {
    void saveAllBatch(List<ChatRoom> chatRooms, Message message);

    void updateAllBatch(List<ChatRoom> updateChatRooms, Message message);
}
