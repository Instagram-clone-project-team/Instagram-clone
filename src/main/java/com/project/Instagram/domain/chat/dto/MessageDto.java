package com.project.Instagram.domain.chat.dto;

import com.project.Instagram.domain.member.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

}
