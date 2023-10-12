package com.project.Instagram.domain.member.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Profile {
    private String username;
    private String image;
    private String introduce;

    public static Profile convertMemberToProfile(Member member){
        return new Profile(member.getUsername(),member.getImage(), member.getIntroduce());
    }
}
