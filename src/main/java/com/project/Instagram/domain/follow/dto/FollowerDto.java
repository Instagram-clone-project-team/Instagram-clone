package com.project.Instagram.domain.follow.dto;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FollowerDto {
    private Profile member;
    private boolean isFollowing;
    private boolean isFollower;
    private boolean isMe;

    @QueryProjection
    public FollowerDto(Member member, boolean isFollowing, boolean isFollower, boolean isMe) {
        this.member = new Profile(member);
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.isMe = isMe;
    }
}
