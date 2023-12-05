package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.entity.CommentLike;
import com.project.Instagram.domain.comment.repository.CommentLikeRepository;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.MemberRole;
import com.project.Instagram.domain.mention.service.MentionService;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.domain.post.service.HashtagService;
import com.project.Instagram.global.error.BusinessException;
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

import static com.project.Instagram.global.error.ErrorCode.COMMENTLIKE_ALREADY_EXIST;
import static com.project.Instagram.global.error.ErrorCode.POST_DELETE_FAILED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {
    @InjectMocks
    CommentLikeService commentLikeService;

    @Mock
    SecurityUtil securityUtil;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    AlarmService alarmService;
    // 윤영

    // 동엽

    // 하늘
    @Test
    @DisplayName("test create comment like:success")
    void test_create_comment_like() {
        Member loginMember = Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .build();
        loginMember.setId(1L);
        Member postWriter = Member.builder()
                .username("post writer")
                .name("haneul2")
                .email("haha2@gmail.com")
                .password("pwd123456789")
                .build();
        postWriter.setId(2L);
        Member commentWriter = Member.builder()
                .username("comment writer")
                .name("haneul3")
                .email("haha3@gmail.com")
                .password("pwd123456789")
                .build();
        Post post = Post.builder()
                .member(postWriter)
                .image("image")
                .content("content")
                .build();
        post.setId(2L);
        Comment comment = Comment.builder()
                .writer(commentWriter)
                .postId(post.getId())
                .build(); //댓글

        given(securityUtil.getLoginMember()).willReturn(loginMember);
        given(commentRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(comment));
        given(postRepository.findByIdAndDeletedAtIsNull(post.getId())).willReturn(Optional.of(post));

        //when
        commentLikeService.createCommentLike(Mockito.anyLong());

        //then
        verify(commentLikeRepository, atLeastOnce()).save(Mockito.any());
        verify(alarmService, atLeastOnce()).sendCommentAlarm(AlarmType.LIKE_COMMENT, loginMember, commentWriter, post, comment);
    }

    @Test
    @DisplayName("test create comment like:[exception]commentlike already exist")
    void test_create_comment_like_throw_commentlike_already_exist() {
        Member loginMember = Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .build();
        loginMember.setId(1L);
        Member postWriter = Member.builder()
                .username("post writer")
                .name("haneul2")
                .email("haha2@gmail.com")
                .password("pwd123456789")
                .build();
        postWriter.setId(2L);
        Member commentWriter = Member.builder()
                .username("comment writer")
                .name("haneul3")
                .email("haha3@gmail.com")
                .password("pwd123456789")
                .build();
        Post post = Post.builder()
                .member(postWriter)
                .image("image")
                .content("content")
                .build();
        post.setId(2L);
        Comment comment = Comment.builder()
                .writer(commentWriter)
                .postId(post.getId())
                .build(); //댓글
        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .member(loginMember)
                .build();

        given(securityUtil.getLoginMember()).willReturn(loginMember);
        given(commentRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(comment));
        given(commentLikeRepository.findByCommentAndMember(comment, loginMember)).willReturn(Optional.ofNullable(commentLike));

        //when
        Throwable exception = assertThrows(BusinessException.class, () -> {
            commentLikeService.createCommentLike(Mockito.anyLong());
        });

        //then
        assertEquals(exception.getMessage(), COMMENTLIKE_ALREADY_EXIST.getMessage());
        verify(commentLikeRepository, never()).save(Mockito.any());
    }
}