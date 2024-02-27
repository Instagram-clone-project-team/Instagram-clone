package com.project.Instagram.domain.chat.dto;

import com.project.Instagram.domain.chat.entity.RoomMember;
import com.project.Instagram.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomDto {
    private Long roomId;
    private String lastMessage;
    private MemberSimpleInfo inviter;
    private List<MemberSimpleInfo> members;

    public ChatRoomDto(Long roomId, String lastMessage, Member inviter, List<RoomMember> roomMembers) {
        this.roomId = roomId;
        this.lastMessage = lastMessage;
        this.inviter = new MemberSimpleInfo(inviter);
        List<MemberSimpleInfo> memberInfo = new ArrayList<>();
        for(RoomMember roomMember : roomMembers) {
            if (roomMember.getMember() != inviter) memberInfo.add(new MemberSimpleInfo(roomMember.getMember()));
        }
        this.members = memberInfo;
    }
}
