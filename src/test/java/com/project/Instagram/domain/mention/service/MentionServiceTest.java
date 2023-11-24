package com.project.Instagram.domain.mention.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.util.StringExtractUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentionServiceTest {

    @InjectMocks
    private MentionService mentionService;

    @Mock
    private AlarmService alarmService;

    @Mock
    private StringExtractUtil stringExtractUtil;

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
}