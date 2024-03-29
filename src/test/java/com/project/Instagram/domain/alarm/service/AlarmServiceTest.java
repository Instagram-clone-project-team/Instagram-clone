package com.project.Instagram.domain.alarm.service;

import com.project.Instagram.domain.alarm.dto.AlarmDto;
import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.alarm.repository.AlarmRepository;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    @Mock
    private FollowRepository followRepository;

    @Nested
    class SendPostLikeAlarm {
        @Test
        @DisplayName("게시물 좋아요 알림 성공 테스트")
        void sendPostLikeAlarmSuccess() {
            // given
            Member agent = Member.builder()
                    .username("testUsername")
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

            Alarm alarm = Alarm.builder()
                    .type(LIKE_POST)
                    .agent(agent)
                    .target(target)
                    .post(post)
                    .build();

            // when
            when(alarmRepository.save(any())).thenReturn(alarm);
            alarmService.sendPostLikeAlarm(LIKE_POST, agent, target, postLike);

            // then
            verify(alarmRepository, times(1)).save(any(Alarm.class));
            verify(alarmRepository).save(argThat(savedAlarm ->
                    savedAlarm.getType() == LIKE_POST &&
                            savedAlarm.getAgent() == agent &&
                            savedAlarm.getTarget() == target &&
                            savedAlarm.getPost() == post));

            String message = alarm.getType().createAlarmMessage(alarm);

            String expectedMessage = LIKE_POST.getMessageTemplate()
                    .replace("{agent.username}", agent.getUsername())
                    .replace("{post.content}", post.getContent());

            assertEquals(expectedMessage, message);
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

                Alarm alarm = Alarm.builder()
                        .type(COMMENT)
                        .agent(agent)
                        .target(target)
                        .post(post)
                        .comment(comment)
                        .build();

                // when
                alarmService.sendCommentAlarm(alarm.getType(), agent, target, post, comment);

                // then
                verify(alarmRepository, times(1)).save(any(Alarm.class));
                verify(alarmRepository).save(argThat(savedAlarm ->
                        savedAlarm.getType() == COMMENT &&
                                savedAlarm.getAgent() == agent &&
                                savedAlarm.getTarget() == target &&
                                savedAlarm.getPost() == post));

                String expectedMessage = COMMENT.getMessageTemplate()
                        .replace("{agent.username}", agent.getUsername())
                        .replace("{comment.text}", comment.getText());

                assertEquals(expectedMessage, alarm.getType().createAlarmMessage(alarm));
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
            Member agent = Member.builder()
                    .username("agentUsername")
                    .build();

            List<String> targetUsernames = Arrays.asList("targetUser1", "targetUser2", "targetUser3");
            List<Member> targetMembers = targetUsernames.stream()
                    .map(username -> Member.builder().username(username).build())
                    .collect(Collectors.toList());

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

            Alarm alarm = Alarm.builder()
                    .type(MENTION_COMMENT)
                    .agent(agent)
                    .post(post)
                    .comment(comment)
                    .build();

            // when
            when(memberRepository.findAllByUsernameIn(targetUsernames)).thenReturn(targetMembers);

            alarmService.sendMentionCommentAlarm(MENTION_COMMENT, agent, targetUsernames, post, comment);
            // then
            verify(alarmRepository, times(targetMembers.size())).save(any(Alarm.class));

            for (Member targetMember : targetMembers) {
                verify(alarmRepository).save(argThat(savedAlarm ->
                        savedAlarm.getType() == MENTION_COMMENT &&
                                savedAlarm.getAgent() == agent &&
                                savedAlarm.getTarget() == targetMember &&
                                savedAlarm.getPost() == post));
            }

            String expectedMessage = MENTION_COMMENT.getMessageTemplate()
                    .replace("{agent.username}", agent.getUsername())
                    .replace("{comment.text}", comment.getText());

            assertEquals(expectedMessage, alarm.getType().createAlarmMessage(alarm));
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
    @DisplayName("팔로우 알림 삭제 테스트")
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
        Member agent = Member.builder()
                .username("agentUsername")
                .build();

        Member target = Member.builder()
                .username("targetUsername")
                .build();

        Follow follow = Follow.builder()
                .member(agent)
                .followMember(target)
                .build();

        Alarm alarm = Alarm.builder()
                .type(FOLLOW)
                .agent(agent)
                .target(target)
                .follow(follow)
                .build();
        //when
        alarmService.sendFollowAlarm(agent, target, follow);

        //then
        verify(alarmRepository).save(Mockito.any());

        String expectedMessage = FOLLOW.getMessageTemplate()
                .replace("{agent.username}", agent.getUsername());

        assertEquals(expectedMessage, alarm.getType().createAlarmMessage(alarm));

    }

    @Test
    @DisplayName("[alarm] send mention post alarm:success")
    void test_send_mention_post_alarm_success() {
        //given
        Member agent = Member.builder()
                .username("agentUsername")
                .build();

        Post post = Post.builder()
                .member(agent)
                .image("postImage")
                .content("postContent")
                .build();

        List<String> targetUsernames = Arrays.asList("targetUser1", "targetUser2", "targetUser3");
        List<Member> targetMembers = targetUsernames.stream()
                .map(username -> Member.builder().username(username).build())
                .collect(Collectors.toList());

        Alarm alarm = Alarm.builder()
                .type(MENTION_POST)
                .agent(agent)
                .post(post)
                .build();

        // when
        when(memberRepository.findAllByUsernameIn(targetUsernames)).thenReturn(targetMembers);
        alarmService.sendMentionPostAlarm(MENTION_POST, agent, targetUsernames, post);

        // then
        verify(alarmRepository, times(targetMembers.size())).save(any(Alarm.class));

        for (Member targetMember : targetMembers) {
            verify(alarmRepository).save(argThat(savedAlarm ->
                    savedAlarm.getType() == MENTION_POST &&
                            savedAlarm.getAgent() == agent &&
                            savedAlarm.getTarget() == targetMember &&
                            savedAlarm.getPost() == post));
        }

        String expectedMessage = MENTION_POST.getMessageTemplate()
                .replace("{agent.username}", agent.getUsername())
                .replace("{post.content}", post.getContent());

        assertEquals(expectedMessage, alarm.getType().createAlarmMessage(alarm));
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
        @DisplayName("알람 가져오기 테스트")
        void validGetArlams(){
            int page = 0;
            int size = 6;
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("exex44");
            Member agentMember = new Member();
            agentMember.setUsername("exex22");
            agentMember.setId(2L);
            Post post = new Post();
            post.setId(1L);
            Comment comment = Comment.builder().writer(agentMember).postId(post.getId()).text("@exex44 테스트").build();
            Alarm alarm1 = Alarm.builder()
                    .post(post)
                    .agent(agentMember)
                    .target(loginMember)
                    .type(LIKE_POST).build();
            Alarm alarm2 = Alarm.builder()
                    .post(post)
                    .agent(loginMember)
                    .target(agentMember)
                    .type(LIKE_POST).build();
            List<Alarm> alarms = new ArrayList<>();
            alarms.add(alarm1);
            alarms.add(alarm2);
            Follow follow1 = Follow.builder().member(loginMember).followMember(agentMember).build();
            Follow follow2 = Follow.builder().member(agentMember).followMember(loginMember).build();

            List<Follow> follows = new ArrayList<>();
            follows.add(follow1);
            follows.add(follow2);

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Alarm> alarmPage = new PageImpl<>(alarms, pageable, alarms.size());
            List<Long> agentIds = new ArrayList<>();

            when(alarmRepository.findByTargetId(loginMember.getId(),pageable)).thenReturn(alarmPage);
            when(securityUtil.getLoginMember()).thenReturn(loginMember);
            when(followRepository.findByMemberIdAndFollowMemberIdIn(loginMember.getId(), agentIds)).thenReturn(follows);
            //when
            Page<AlarmDto> responseDtos = alarmService.getAlarms(page,size);

            //then
            List<AlarmDto> testDto =responseDtos.getContent();
            assertNotNull(responseDtos);
            assertEquals(size,responseDtos.getSize());
            assertEquals(page,responseDtos.getTotalPages()-1);
            assertEquals(alarms.get(0).getType().toString(),testDto.get(0).getType());
            assertEquals(alarms.get(0).getAgent().getId(),agentMember.getId());
            assertEquals(alarms.get(0).getTarget().getId(),loginMember.getId());
            verify(alarmRepository, times(1)).findByTargetId(anyLong(), any(Pageable.class));
            verify(followRepository, times(1)).findByMemberIdAndFollowMemberIdIn(anyLong(), anyList());

        }
    }
}