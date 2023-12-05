package com.project.Instagram.domain.mention.service;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.util.StringExtractUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;

import static com.project.Instagram.domain.alarm.dto.AlarmType.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentionServiceTest {
    @InjectMocks
    private MentionService mentionService;
    @Mock
    private StringExtractUtil stringExtractUtil;
    @Mock
    private AlarmService alarmService;

    @Test
    @DisplayName("게시물 언급 성공 테스트")
    void checkUpdateMentionsFromPostSuccess() {
        // given
        Member agent = Member.builder()
                .username("agentUsername")
                .build();

        Post post = Post.builder()
                .member(agent)
                .image("postImage")
                .content("postContent")
                .build();
        post.setId(1L);

        String beforeText = "beforeText @user1 @user2";
        String afterText = "afterText @user1 @user3";

        when(stringExtractUtil.filteringMentions(beforeText))
                .thenReturn(Arrays.asList("user1", "user2"));
        when(stringExtractUtil.filteringMentions(afterText))
                .thenReturn(Arrays.asList("user1", "user3"));
        // when
        mentionService.checkUpdateMentionsFromPost(agent, beforeText, afterText, post);

        // then
        verify(alarmService, times(1)).sendMentionPostAlarm(
                eq(MENTION_COMMENT),
                eq(agent),
                eq(List.of("user3")),
                eq(post)
        );
    }

    @Test
    @DisplayName("게시물 언급 실패 테스트")
    void checkUpdateMentionsFromPostFail() {
        // given
        Member agent = Member.builder()
                .username("agentUsername")
                .build();

        Post post = Post.builder()
                .member(agent)
                .image("postImage")
                .content("postContent")
                .build();
        post.setId(1L);

        String beforeText = "beforeText @user1 @user2";
        String afterText = "afterText @user1 @user2";

        when(stringExtractUtil.filteringMentions(beforeText))
                .thenReturn(Arrays.asList("user1", "user2"));
        when(stringExtractUtil.filteringMentions(afterText))
                .thenReturn(Arrays.asList("user1", "user2"));
        // when
        mentionService.checkUpdateMentionsFromPost(agent, beforeText, afterText, post);

        // then
        verify(alarmService, never()).sendMentionPostAlarm(
                eq(MENTION_COMMENT),
                eq(agent),
                eq(Arrays.asList("user1", "user2")),
                eq(post)
        );
    }

    @Test
    @DisplayName("[mention] check update mantions from comment:success")
    void test_check_update_mentions_from_comment_success(){
        //given
        Member member=new Member();
        Post post=new Post();
        Comment comment=new Comment();
        String beforeText="수정 전";
        String afterText="수정 후";
        //when
        mentionService.checkUpdateMentionsFromComment(member, beforeText, afterText, post, comment );
        //then
        verify(alarmService).sendMentionCommentAlarm(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any());
    }

    @Test
    @DisplayName("[mention] check mentions from post:success")
    void test_check_mentions_from_post_success(){
        //given
        Member member=new Member();
        Post post=new Post();
        String content="게시글 수정";
        //when
        mentionService.checkMentionsFromPost(member,content, post);
        //then
        verify(alarmService).sendMentionPostAlarm(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Nested
    class checkMentionsFromComment{
        @Test
        @DisplayName("댓글 멘션 동작 테스트")
        void sendAlram(){
            //given
            Member agent = Member.builder()
                    .username("exex11")
                    .build();
            Post post = new Post();
            Comment comment = new Comment();
            comment.updateText("@exex22 @e2e222");
            List<String> mentionTargets = Arrays.asList("exex22","e2e222");
            when(stringExtractUtil.filteringMentions(comment.getText())).thenReturn(mentionTargets);

            //when
            mentionService.checkMentionsFromComment(agent, comment.getText(), post,comment);

            //then
            verify(alarmService,times(1)).sendMentionCommentAlarm(
                    eq(AlarmType.MENTION_COMMENT),
                    eq(agent),
                    eq(List.of("exex22","e2e222")),
                    eq(post),
                    eq(comment)
            );
        }
    }
}