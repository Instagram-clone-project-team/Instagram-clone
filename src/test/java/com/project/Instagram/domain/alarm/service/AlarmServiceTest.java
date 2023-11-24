package com.project.Instagram.domain.alarm.service;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.alarm.repository.AlarmRepository;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.util.SecurityUtil;
import com.project.Instagram.global.util.StringExtractUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @InjectMocks
    AlarmService alarmService;

    @Mock
    AlarmRepository alarmRepository;

    @Mock
    MemberRepository memberRepository;
    @Mock
    FollowRepository followRepository;
    @Mock
    SecurityUtil securityUtil;
    @Mock
    StringExtractUtil stringExtractUtil;

    @Test
    @DisplayName("[alarm] send follow alarm:success")
    void test_send_follow_alarm_success() {
        //given
        Member agent = new Member();
        Member target = new Member();
        Follow follow = new Follow();
        //when
        alarmService.sendFollowAlarm(agent, target, follow);
        //then
        verify(alarmRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("[alarm] send mention post alarm:success")
    void test_send_mention_post_alarm_success() {
        //given
        Member agent = new Member();
        Post post = new Post();
        List<Member> foundMemberList = new ArrayList<>();
        foundMemberList.add(new Member());
        foundMemberList.add(new Member());
        foundMemberList.add(new Member());
        foundMemberList.add(new Member());
        given(memberRepository.findAllByUsernameIn(Mockito.any())).willReturn(foundMemberList);

        //when
        alarmService.sendMentionPostAlarm(AlarmType.MENTION_POST, agent, Mockito.any(), post);

        //then
        verify(alarmRepository, times(foundMemberList.size())).save(Mockito.any());
    }

    @Test
    @DisplayName("[alarm] delete post like alarm:success")
    void test_delete_post_like_alarm_success() {
        //given
        Member agent = new Member();
        Member target = new Member();
        Post post = new Post();

        //when
        alarmService.deletePostLikeAlarm(AlarmType.LIKE_POST, agent, target, post);

        //then
        verify(alarmRepository).deleteByTypeAndAgentAndTargetAndPost(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("[alarm] delete comment like alarm:success")
    void test_delete_comment_like_alarm_success() {
        //given
        Member agent = new Member();
        Member target = new Member();
        Comment comment = new Comment();

        //when
        alarmService.deleteCommentLikeAlarm(AlarmType.LIKE_COMMENT, agent, target, comment);

        //then
        verify(alarmRepository).deleteByTypeAndAgentAndTargetAndComment(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("[alarm] delete all comment alarm:success")
    void test_delete_all_comment_alarm_success() {
        //given
        List<Comment> comments = new ArrayList<>();
        List<Alarm> alarms = new ArrayList<>();
        given(alarmRepository.findAllByCommentIn(Mockito.any())).willReturn(alarms);

        //when
        alarmService.deleteAllCommentAlarm(comments);

        //then
        verify(alarmRepository).findAllByCommentIn(Mockito.any());
        verify(alarmRepository).deleteAllInBatch(Mockito.any());
    }
}