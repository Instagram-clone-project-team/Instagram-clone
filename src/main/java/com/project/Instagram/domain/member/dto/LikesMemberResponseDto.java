package com.project.Instagram.domain.member.dto;

import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikesMemberResponseDto {
    private Profile profile;

    public LikesMemberResponseDto(Member member){
        this.profile = Profile.convertMemberToProfile(member);
    }
}
