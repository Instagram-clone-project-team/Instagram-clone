package com.project.Instagram.domain.mention.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.util.StringExtractUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
}