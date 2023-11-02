package com.project.Instagram.domain.search.dto;

import com.project.Instagram.domain.follow.dto.FollowDto;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SearchMemberDto extends SearchDto {
    private Profile member;
    private boolean isFollowing;
    private boolean isFollower;
    private List<FollowDto> followingMemberFollow;
    private int followingMemberFollowCount;

    @QueryProjection
    public SearchMemberDto(String dtype, Member member, boolean isFollowing, boolean isFollower) {
        super(dtype);
        this.member = new Profile(member);
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
    }

    public void setFollowingMemberFollow(List<FollowDto> followingMemberFollow, int maxCount) {
        if (followingMemberFollow == null) {
            this.followingMemberFollow = Collections.emptyList();
            this.followingMemberFollowCount = 0;
            return;
        }
        this.followingMemberFollow = followingMemberFollow
                .stream()
                .limit(maxCount)
                .collect(Collectors.toList());

        this.followingMemberFollowCount = followingMemberFollow.size() - this.followingMemberFollow.size();
    }
}
