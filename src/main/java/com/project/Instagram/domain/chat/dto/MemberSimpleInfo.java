package com.project.Instagram.domain.chat.dto;


import com.project.Instagram.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSimpleInfo {

    private String username;
    private String name;
    private String image;

    public MemberSimpleInfo(Member member) {
        this.username = member.getUsername();
        this.name = member.getName();
        this.image = member.getImage();
    }
}
