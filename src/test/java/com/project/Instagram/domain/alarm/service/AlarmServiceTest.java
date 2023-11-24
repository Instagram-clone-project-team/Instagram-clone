package com.project.Instagram.domain.alarm.service;

import com.project.Instagram.domain.alarm.dto.AlarmDto;
import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.alarm.repository.AlarmRepository;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import com.project.Instagram.global.util.StringExtractUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.project.Instagram.domain.alarm.dto.AlarmType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {
    @InjectMocks
    private AlarmService alarmService;
    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private StringExtractUtil stringExtractUtil;

    @Nested
    class SendPostLikeAlarm {
        @Test
        @DisplayName("게시물 좋아요 알림 성공 테스트")
        void sendPostLikeAlarmSuccess() {
            // given
            Member agent = Member.builder()
                    .username("agentUsername")
                    .build();

            Member target = Member.builder()
                    .username("targetUsername")
                    .build();

            Post post = Post.builder()
                    .member(agent)
                    .image("postImage")
                    .content("postContent")
                    .build();

            PostLike postLike = PostLike.builder()
                    .member(target)
                    .post(post)
                    .build();

            // when
            alarmService.sendPostLikeAlarm(LIKE_POST, agent, target, postLike);

            // then
            verify(alarmRepository, times(1)).save(any(Alarm.class));

            verify(alarmRepository).save(argThat(savedAlarm ->
                    savedAlarm.getType() == LIKE_POST &&
                    savedAlarm.getAgent() == agent &&
                    savedAlarm.getTarget() == target &&
                    savedAlarm.getPost() == post));
        }

        @Test
        @DisplayName("게시물 좋아요 알림 실패 테스트")
        void sendPostLikeAlarmFail() {
            // given
            Member agent = Member.builder()
                    .username("agentUsername")
                    .build();

            Member target = Member.builder()
                    .username("targetUsername")
                    .build();

            Post post = Post.builder()
                    .member(agent)
                    .image("postImage")
                    .content("postContent")
                    .build();

            PostLike postLike = PostLike.builder()
                    .member(target)
                    .post(post)
                    .build();

            // when, then
            assertThrows(BusinessException.class, () -> {
                alarmService.sendPostLikeAlarm(LIKE_COMMENT, agent, target, postLike);
            });

            // then
            verify(alarmRepository, never()).save(any(Alarm.class));
        }

        @Nested
        class SendCommentAlarm {
            @Test
            @DisplayName("댓글 알림 성공 테스트")
            void sendCommentAlarmSuccess() throws NoSuchFieldException, IllegalAccessException {
                // given
                AlarmType type = COMMENT;

                Member agent = Member.builder()
                        .username("agentUsername")
                        .build();

                Member target = Member.builder()
                        .username("targetUsername")
                        .build();

                Post post = Post.builder()
                        .member(agent)
                        .image("postImage")
                        .content("postContent")
                        .build();
                post.setId(1L);

                Comment comment = Comment.builder()
                        .writer(agent)
                        .postId(post.getId())
                        .text("Comment text")
                        .build();

                // when
                alarmService.sendCommentAlarm(type, agent, target, post, comment);

                // then
                verify(alarmRepository, times(1)).save(any(Alarm.class));

                verify(alarmRepository).save(argThat(savedAlarm ->
                        savedAlarm.getType() == type &&
                                savedAlarm.getAgent() == agent &&
                                savedAlarm.getTarget() == target &&
                                savedAlarm.getPost() == post &&
                                savedAlarm.getComment() == comment));
            }


            @Test
            @DisplayName("댓글 알림 실패 테스트")
            void sendCommentAlarmFail() {

                // given
                AlarmType type = LIKE_POST;

                Member agent = Member.builder()
                        .username("agentUsername")
                        .build();

                Member target = Member.builder()
                        .username("targetUsername")
                        .build();

                Post post = Post.builder()
                        .member(agent)
                        .image("postImage")
                        .content("postContent")
                        .build();
                post.setId(1L);

                Comment comment = Comment.builder()
                        .writer(agent)
                        .postId(post.getId())
                        .text("Comment text")
                        .build();

                // when
                assertThrows(BusinessException.class, () -> {
                    alarmService.sendCommentAlarm(type, agent, target, post, comment);
                });
                // then
                verify(alarmRepository, never()).save(any(Alarm.class));
            }
        }
    }

    @Nested
    class SendMentionCommentAlarm {
        @Test
        @DisplayName("댓글 언급 알림 성공 테스트")
        void sendMentionCommentAlarmSuccess() {
            // given
            AlarmType type = MENTION_COMMENT;

            Member agent = Member.builder()
                    .username("agentUsername")
                    .build();

            List<String> targets = Arrays.asList("targetUsername1", "targetUsername2");
            Member targetMember1 = Member.builder().username("targetUsername1").build();
            Member targetMember2 = Member.builder().username("targetUsername2").build();

            when(memberRepository.findAllByUsernameIn(targets)).thenReturn(Arrays.asList(targetMember1, targetMember2));

            Post post = Post.builder()
                    .member(agent)
                    .image("postImage")
                    .content("postContent")
                    .build();
            post.setId(1L);

            Comment comment = Comment.builder()
                    .writer(agent)
                    .postId(post.getId())
                    .text("Comment text")
                    .build();

            // when
            alarmService.sendMentionCommentAlarm(type, agent, targets, post, comment);

            // then
            verify(alarmRepository, times(2)).save(any(Alarm.class));
        }

        @Test
        @DisplayName("댓글 언급 실패 테스트")
        void sendMentionCommentAlarmFail() {
            // given
            AlarmType type = LIKE_COMMENT;

            Member agent = Member.builder()
                    .username("agentUsername")
                    .build();

            List<String> targets = Collections.singletonList("nonExistentUsername");

            Post post = Post.builder()
                    .member(agent)
                    .image("postImage")
                    .content("postContent")
                    .build();
            post.setId(1L);

            Comment comment = Comment.builder()
                    .writer(agent)
                    .postId(post.getId())
                    .text("Comment text")
                    .build();

            // when, then
            assertThrows(BusinessException.class, () -> {
                alarmService.sendMentionCommentAlarm(type, agent, targets, post, comment);
            });

            verify(alarmRepository, never()).save(any(Alarm.class));
        }
    }

    @Test
    @DisplayName("팔오우 알림 삭제 테스트")
    void deleteFollowAlarm() {
        // given
        Member agent = Member.builder()
                .username("agentUsername")
                .build();

        Member target = Member.builder()
                .username("targetUsername")
                .build();

        Follow follow = new Follow(agent, target);

        // when
        alarmService.deleteFollowAlarm(agent, target, follow);

        // then
        verify(alarmRepository).deleteByTypeAndAgentAndTargetAndFollow(FOLLOW, agent, target, follow);
    }

    @Test
    @DisplayName("게시물 알림 전체 삭제 테스트")
    void deleteAllPostAlarm() {
        // given
        Post post = Post.builder()
                .member(Member.builder().username("username").build())
                .image("postImage")
                .content("postContent")
                .build();

        List<Alarm> alarms = Arrays.asList(
                Alarm.builder().type(LIKE_POST).post(post).build(),
                Alarm.builder().type(LIKE_POST).post(post).build()
        );
        when(alarmRepository.findAllByPost(post)).thenReturn(alarms);

        // when
        alarmService.deleteAllPostAlarm(post);
        // then
        verify(alarmRepository).deleteAllInBatch(alarms);
    }

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

    @Nested
    class getArlamsOnLike{
        @Test
        @DisplayName(" 게시글 좋아요 알람 가져오기 테스트")
        void validGetArlamsOnPostLike(){
            Member loginMember = Member.builder()
                    .name("사사사")
                    .username("exex22")
                    .password("qwer1234").build();
            loginMember.setId(1L);
            Post post = Post.builder().build();
            post.setId(1L);

            Alarm alarm = Alarm.builder()
                    .post(post)
                    .agent(loginMember)
                    .type(LIKE_POST).build();
            List<Alarm> alarms = new ArrayList<>();
            int page = 0;
            int size = 6;
            alarms.add(alarm);

            when(securityUtil.getLoginMember()).thenReturn(loginMember);
            Pageable pageable = PageRequest.of(page, size);
            Page<Alarm> response = new PageImpl<>(alarms);
            when(alarmRepository.findByTargetId(loginMember.getId(),pageable)).thenReturn(response);

            //when
            Page<AlarmDto> responseDto = alarmService.getAlarms(page,size);

            //then
            List<AlarmDto> testDto =responseDto.getContent();
            assertEquals(testDto,response);
        }
    }
    @Nested
    class getArlamsOnMention{

    }
    @Nested
    class getArlamsOncComment{

    }
    @Nested
    class getArlamsOnFollow{

    }
}