package com.project.Instagram.domain.follow.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.entity.PageInfo;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    @Nested
    class Unfollow {
        @Test
        @DisplayName("unfollow() 성공 테스트")
        void unfollowSuccess() {
            // given
            Member member = spy(new Member());
            member.setId(1L);
            member.setUsername("testUser");
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            Member followMember = spy(new Member());
            followMember.setId(2L);
            followMember.setUsername("followMember");
            when(memberRepository.findByUsername(followMember.getUsername())).thenReturn(Optional.of(followMember));

            Follow follow = Follow.builder()
                    .member(member)
                    .followMember(followMember)
                    .build();

            when(securityUtil.getLoginMember()).thenReturn(member);
            when(followRepository.findByMemberIdAndFollowMemberId(member.getId(), followMember.getId())).thenReturn(Optional.of(follow));

            // when
            boolean result = followService.unfollow(followMember.getUsername());

            // then
            assertTrue(result);
            verify(member, times(1)).decreaseFollowingCount();
            verify(followMember, times(1)).decreaseFollowerCount();
            verify(memberRepository, times(1)).findById(anyLong());
            verify(memberRepository, times(1)).findByUsername(anyString());
            verify(followRepository, times(1)).findByMemberIdAndFollowMemberId(anyLong(), anyLong());
            verify(followRepository, times(1)).delete(any(Follow.class));
            verify(alarmService, times(1)).deleteFollowAlarm(any(Member.class), any(Member.class), any(Follow.class));
        }

        @Test
        @DisplayName("unfollow() member not found 예외")
        void unfollowMemberNotFound() {
            // given
            Member member = new Member();
            member.setId(1L);
            member.setUsername("testName");
            when(securityUtil.getLoginMember()).thenReturn(member);
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
            when(memberRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> followService.unfollow("test"))
                    .withMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("unfollow 자기 자신 팔로우 예외")
        void unfollowMyselfFail() {
            // given
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("testName");
            when(securityUtil.getLoginMember()).thenReturn(loginMember);
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(loginMember));
            when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(loginMember));
            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> followService.unfollow("test"))
                    .withMessage(UNFOLLOW_MYSELF_FAIL.getMessage());
        }

        @Test
        @DisplayName("unfollow UnFollowFail 예외")
        void unfollowFail() {
            // given
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("LoginName");
            when(securityUtil.getLoginMember()).thenReturn(loginMember);

            Member followMember = new Member();
            followMember.setId(2L);
            followMember.setUsername("followName");
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(loginMember));
            when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(followMember));

            when(followRepository.findByMemberIdAndFollowMemberId(anyLong(), anyLong())).thenReturn(Optional.empty());
            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> followService.unfollow("test"))
                    .withMessage(UNFOLLOW_FAIL.getMessage());
        }
    }

    @Nested
    class GetFollowings {
        @Test
        @DisplayName("getFollowings() 성공 테스트")
        void getFollowings() {
            // given
            int page = 1;
            int size = 1;
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("loginName");
            when(securityUtil.getLoginMember()).thenReturn(loginMember);

            Member member = new Member();
            member.setId(2L);
            member.setUsername("testUsername");
            when(memberRepository.findByUsername(any())).thenReturn(Optional.of(member));

            FollowerDto followerDto = new FollowerDto(member, true, false, false);
            Page<FollowerDto> pageResult = new PageImpl<>(Collections.singletonList(followerDto));
            when(followRepository.findFollowings(any(), any(), any(PageRequest.class))).thenReturn(pageResult);

            // when
            PageListResponse<FollowerDto> result = followService.getFollowings(member.getUsername(), page, size);
            PageInfo pageInfo = result.getPageInfo();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getData()).hasSize(1);
            assertThat(pageInfo).isNotNull();
            assertThat(pageInfo.getPage()).isEqualTo(page);
            assertThat(pageInfo.getSize()).isEqualTo(size);
            assertThat(pageInfo.getTotalElements()).isEqualTo(1);
            assertThat(pageInfo.getTotalPages()).isEqualTo(1);
            verify(followRepository).findFollowings(eq(loginMember.getId()), eq(member.getId()), any(PageRequest.class));
        }

        @Test
        @DisplayName("getFollowings() 예외 테스트")
        void getFollowingsMemberNotFound() {
            // given
            int page = 1;
            int size = 5;
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("loginName");
            when(securityUtil.getLoginMember()).thenReturn(loginMember);

            when(memberRepository.findByUsername(any())).thenReturn(Optional.empty());

            // when
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> followService.getFollowings("nonMember", page, size))
                    .withMessage(MEMBER_NOT_FOUND.getMessage());
            // then
            verify(followRepository, never()).findFollowings(any(), any(), any(PageRequest.class));
        }
    }


    // 동엽

    // 하늘
    @Test
    @DisplayName("get followers:success")
    void test_get_followers() {
        //given
        int page = 0;
        int size = 2;
        long memberId = 1L;
        String username = "luee";
        Member member = new Member();
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
    void test_get_followers_count() {
        //given
        String username = "luee";
        Member member = new Member();
        int count = 10;
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));
        when(followRepository.countActiveFollowersByMemberUsername(username)).thenReturn(count);
        //when
        int response = followService.getFollowerCount(username);
        //then
        assertEquals(response, count);
    }
}