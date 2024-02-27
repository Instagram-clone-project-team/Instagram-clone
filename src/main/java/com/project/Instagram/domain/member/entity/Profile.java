package com.project.Instagram.domain.member.entity;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Profile {
    private String username;
    private String image;
    private String introduce;

    public static Profile convertMemberToProfile(Member member){
        return new Profile(member.getUsername(),member.getImage(), member.getIntroduce());
    }

    public static Profile convertMemberToProfile(String username, String image) {
        return new Profile(username, image, null);
    }

    @QueryProjection
    public Profile(Member member) {
        this.username = member.getUsername();
        this.image = member.getImage();
        this.introduce = member.getIntroduce();
    }
}
