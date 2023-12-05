package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.MemberRole;
import com.project.Instagram.domain.mention.service.MentionService;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.domain.post.service.HashtagService;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;
    @Mock
    SecurityUtil securityUtil;
    @Mock
    PostRepository postRepository;
    @Mock
    AlarmService alarmService;
    @Mock
    MentionService mentionService;
    @Mock
    HashtagService hashtagService;

    // 윤영

    // 동엽

    // 하늘
    @Test
    @DisplayName("test create comment:success")
    void test_create_comment(){
        Set<MemberRole> roles = new HashSet<>();
        roles.add(MemberRole.USER);
        Member loginMember = Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .roles(roles)
                .build();
        loginMember.setId(1L);
        Member writer = Member.builder()
                .username("luee2")
                .name("haneul2")
                .email("haha2@gmail.com")
                .password("pwd123456789")
                .roles(roles)
                .build();
        writer.setId(2L);
        Post post = Post.builder()
                .member(writer)
                .image("image")
                .content("content")
                .build();
        post.setId(2L);

        given(securityUtil.getLoginMember()).willReturn(loginMember);
        given(postRepository.findByIdAndDeletedAtIsNull(post.getId())).willReturn(Optional.of(post));
        String text="comment";
        long postId=post.getId();
        //when
        commentService.createComment(text, postId);

        //then
        verify(commentRepository).save(Mockito.any());
        verify(hashtagService).registerHashTagOnComment(Mockito.any(), Mockito.anyString());
        verify(alarmService).sendCommentAlarm(eq(AlarmType.COMMENT), eq(loginMember), eq(writer), eq(post), Mockito.any());
        verify(mentionService).checkMentionsFromComment(eq(loginMember), eq(text), eq(post), Mockito.any());
    }
}