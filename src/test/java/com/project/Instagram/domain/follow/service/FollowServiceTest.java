package com.project.Instagram.domain.follow.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {
    @InjectMocks
    FollowService followService;
    @Mock
    FollowRepository followRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    AlarmService alarmService;
    @Mock
    SecurityUtil securityUtil;

    // 윤영

    // 동엽

    // 하늘
    @Test
    @DisplayName("get followers:success")
    void test_get_followers(){
        //given
        int page = 0;
        int size = 2;
        long memberId=1L;
        String username="luee";
        Member member=new Member();
        Member member1 = new Member();
        Member member2 = new Member();
        Member member3 = new Member();
        List<FollowerDto> data = new ArrayList<>();
        data.add(new FollowerDto(member1, true, true, false));
        data.add(new FollowerDto(member2, true, true, false));
        data.add(new FollowerDto(member3, true, true, false));
        Page<FollowerDto> pageInfo = new PageImpl<>(data, PageRequest.of(page, size), data.size());
        //when(securityUtil.getLoginMember()).thenReturn(member);
        //when(member.getId()).thenReturn(memberId);
        when(securityUtil.getLoginMember().getId()).thenReturn(memberId);
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));
        when(followRepository.findFollowers(memberId, member.getId(), PageRequest.of(page, size))).thenReturn(pageInfo);
        //when
        PageListResponse<FollowerDto> response = followService.getFollowers(username, page, size);
        //then
        assertNotNull(response);
        assertEquals(size, response.getPageInfo().getSize());
        assertEquals(data.size(), response.getPageInfo().getTotalElements());
        assertEquals(data.get(0).isFollowing(), response.getData().get(0).isFollowing());
        assertEquals(page, response.getPageInfo().getPage() - 1);
        verify(memberRepository, atLeastOnce()).findByUsername(username);
        verify(followRepository, atLeastOnce()).findFollowers(memberId, member.getId(), PageRequest.of(page, size));
    }

    @Test
    @DisplayName("get follower count:success")
    void test_get_followers_count(){
        //given
        String username="luee";
        Member member=new Member();
        int count=10;
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));
        when(followRepository.countActiveFollowersByMemberUsername(username)).thenReturn(count);
        //when
        int response=followService.getFollowerCount(username);
        //then
        assertEquals(response, count);
    }
}