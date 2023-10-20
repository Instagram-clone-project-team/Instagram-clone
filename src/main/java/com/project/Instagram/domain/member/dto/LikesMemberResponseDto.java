package com.project.Instagram.domain.member.dto;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.project.Instagram.domain.member.entity.Profile.convertFromMember;

@Getter
@NoArgsConstructor
public class LikesMemberResponseDto {
    private Profile profile;


    public LikesMemberResponseDto(Member member){
        this.profile = convertFromMember(member);
    }
}
