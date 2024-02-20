package com.project.Instagram.domain.chat.dto;

import com.project.Instagram.domain.chat.entity.mongo.Chatting;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    private Long id;
    @NotNull
    private Long roomId;
    @NotNull
    private String content;
    private String senderUsername;
    private Long senderId;
    private long sendTime;
    private Long readCount;

    public void setSendTimeAndSender(LocalDateTime sendTime, Long senderId, String senderUsername, Long readCount) {
        this.senderUsername = senderUsername;
        this.sendTime = sendTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        this.senderId = senderId;
        this.readCount = readCount;
    }

    public Chatting convertEntity() {
        return Chatting.builder()
                .senderUsername(senderUsername)
                .senderId(senderId)
                .roomId(roomId)
                .content(content)
                .sendDate(Instant.ofEpochMilli(sendTime).atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .readCount(readCount)
                .build();
    }
}
