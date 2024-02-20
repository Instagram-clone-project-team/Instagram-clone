package com.project.Instagram.domain.chat.service;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void connectChatRoom(Long chatRoomId, String email) {
        ChatRoom chatRoom = ChatRoom.builder()
                .email(email)
                .chatroomId(chatRoomId)
                .build();

        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void disconnectChatRoom(Long chatRoomId, String email) {
        ChatRoom chatRoom = chatRoomRepository.findByChatroomIdAndEmail(chatRoomId, email)
                .orElseThrow(IllegalStateException::new);

        chatRoomRepository.delete(chatRoom);
    }

//    public boolean isAllConnected(Long chatRoomId) {
//        List<ChatRoom> connectedList = chatRoomRepository.findByChatroomId(chatRoomId);
//        return connectedList.size() == 2;
//    }
//
//    public boolean isConnected(Long chatRoomId) {
//        List<ChatRoom> connectedList = chatRoomRepository.findByChatroomId(chatRoomId);
//        return connectedList.size() == 1;
//    }
}

