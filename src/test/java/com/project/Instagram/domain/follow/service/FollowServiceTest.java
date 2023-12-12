package com.project.Instagram.domain.follow.sfrvice;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.follow.service.FollowService;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.FOLLOW_MYSELF_FAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {
    @InjectMocks
    FollowService followService;
    @Mock
    FollowRepository followRepository;
    @Mock
    SecurityUtil securityUtil;
    @Mock
    MemberRepository memberRepository;
    @Mock
    AlarmService alarmService;
    // 윤영

    // 동엽
    @Test
    @DisplayName("follow 로직성공")
    void followSuccess(){
        String username = "exex4455";
        Member loginmember = Member.builder()
                .username("exex22")
                .build();
        loginmember.setId(1L);
        loginmember.setFollowingCount(0);
        Member followmember = Member.builder()
                .username(username).build();
        followmember.setId(2L);
        followmember.setFollowerCount(0);

        when(securityUtil.getLoginMember()).thenReturn(loginmember);
        when(memberRepository.findById(loginmember.getId())).thenReturn(Optional.of(loginmember));
        when(memberRepository.findByUsername(followmember.getUsername())).thenReturn(Optional.of(followmember));

        followService.follow(username);

        verify(followRepository).save(any(Follow.class));
        verify(memberRepository).findById(loginmember.getId());
        verify(memberRepository).findByUsername(followmember.getUsername());
        verify(followRepository).existsByMemberIdAndFollowMemberId(loginmember.getId(),followmember.getId());
        verify(alarmService).sendFollowAlarm(any(Member.class),any(Member.class),any(Follow.class));
    }
    @Test
    @DisplayName("follow 로직 실패(자기자신 팔로우)")
    void followFail(){
        String username = "exex4455";
        Member loginmember = Member.builder()
                .username(username)
                .build();
        loginmember.setId(1L);
        loginmember.setFollowingCount(0);

        when(securityUtil.getLoginMember()).thenReturn(loginmember);
        when(memberRepository.findById(loginmember.getId())).thenReturn(Optional.of(loginmember));
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(loginmember));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> followService.follow(username))
                .withMessage(FOLLOW_MYSELF_FAIL.getMessage());
    }
    @Test
    @DisplayName("getFollowingCount")
    void getFollowingCount(){
        String username = "exex4455";
        Member member = Member.builder()
                .username("exex4455")
                .build();
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));
        int result = followService.getFollowingCount(username);
        verify(followRepository).countActiveFollowsByMemberUsername(username);
        assertThat(result).isEqualTo(0);
    }
    // 하늘
}