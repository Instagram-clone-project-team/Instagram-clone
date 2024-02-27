package com.project.Instagram.domain.chat.dto;

import com.project.Instagram.domain.chat.entity.Message;
import com.project.Instagram.domain.member.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.project.Instagram.domain.member.entity.Profile.convertMemberToProfile;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long roomId;
    private Long messageId;
    private Profile sender;
    private String content;
    private LocalDateTime messageDate;

    public MessageDto(Message message, Profile sender) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.sender = convertMemberToProfile(message.getMember().getUsername(), message.getMember().getImage());
        this.content = message.getContent();
        this.messageDate = message.getCreatedDate();
    }
}
