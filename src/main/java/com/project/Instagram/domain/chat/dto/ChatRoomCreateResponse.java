package com.project.Instagram.domain.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateResponse {
    private Long chatRoomId;
    private MemberSimpleInfo inviter;
    private List<MemberSimpleInfo> members = new ArrayList<>();
}
