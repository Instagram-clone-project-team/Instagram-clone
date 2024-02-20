package com.project.Instagram.domain.chat.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@RedisHash(value = "chatRoom")
public class ChatRoom {
    @Id
    private String id;

    @Indexed
    private Long chatroomId;

    @Indexed
    private String email;

    @Builder
    public ChatRoom(Long chatroomId, String email) {
        this.chatroomId = chatroomId;
        this.email = email;
    }
}
