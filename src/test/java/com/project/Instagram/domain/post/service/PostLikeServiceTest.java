package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.MemberRole;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import com.project.Instagram.domain.post.repository.PostLikeRepository;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.project.Instagram.domain.alarm.dto.AlarmType.LIKE_POST;
import static com.project.Instagram.global.error.ErrorCode.POSTLIKE_ALREADY_EXIST;
import static com.project.Instagram.global.error.ErrorCode.POSTLIKE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @InjectMocks
    PostLikeService postLikeService;
    @Mock
    SecurityUtil securityUtil;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    AlarmService alarmService;
    @Mock
    PostRepository postRepository;
    // 윤영

    // 동엽
    @Test
    @DisplayName("좋아요 생성")
    void postlike() {
        Member member = new Member();
        member.setId(1L);
        Member member2 = new Member();
        Post post = new Post();
        post.setMember(member2);
        post.setLikeCount(0);

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(postRepository.findWithMemberById(post.getId())).thenReturn(Optional.of(post));

        postLikeService.postlike(post.getId());

        verify(postLikeRepository).findByMemberAndPost(any(), any());
        verify(postLikeRepository).save(any());
        verify(alarmService).sendPostLikeAlarm(any(), any(), any(), any());
    }

    @Test
    @DisplayName("이미 좋아요 한 상태에서 좋아요 생성")
    void postlikesituationexist() {
        Member member = new Member();
        member.setId(1L);
        Member member2 = new Member();
        Post post = new Post();
        post.setMember(member2);
        post.setLikeCount(0);
        PostLike postLike = new PostLike(member, post);
        when(securityUtil.getLoginMember()).thenReturn(member);
        when(postRepository.findWithMemberById(post.getId())).thenReturn(Optional.of(post));
        when(postLikeRepository.findByMemberAndPost(member, post)).thenReturn(Optional.of(postLike));


        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> postLikeService.postlike(post.getId()))
                .withMessage(POSTLIKE_ALREADY_EXIST.getMessage());
    }

    // 하늘
    @Test
    @DisplayName("post unlike:success")
    void test_post_unlike() {
        Set<MemberRole> roles = new HashSet<>();
        roles.add(MemberRole.USER);
        Member member = Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .roles(roles)
                .build();
        member.setId(1L);
        Member wrtier = Member.builder()
                .username("luee22")
                .name("haneul22")
                .email("haha22@gmail.com")
                .password("pwd1234567822")
                .roles(roles)
                .build();
        member.setId(2L);
        Post post = Post.builder()
                .member(wrtier)
                .image("image")
                .content("content")
                .build();
        post.setLikeCount(10);
        post.setId(2L);
        PostLike postLike = PostLike.builder()
                .member(member)
                .post(post)
                .build();
        given(securityUtil.getLoginMember()).willReturn(member);
        given(postRepository.findWithMemberById(post.getId())).willReturn(Optional.of(post));
        given(postLikeRepository.findByMemberAndPost(member, post)).willReturn(Optional.ofNullable(postLike));
        //when
        postLikeService.postunlike(post.getId());
        //then
        verify(alarmService, times(1)).deletePostLikeAlarm(LIKE_POST, member, post.getMember(), post);
        assertEquals(post.getLikeCount(), 9);
    }

    @Test
    @DisplayName("post unlike:[exception]post like not found")
    void test_post_throw_post_like_not_found() {
        Set<MemberRole> roles = new HashSet<>();
        roles.add(MemberRole.USER);
        Member member = Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .roles(roles)
                .build();
        member.setId(1L);
        Member wrtier = Member.builder()
                .username("luee22")
                .name("haneul22")
                .email("haha22@gmail.com")
                .password("pwd1234567822")
                .roles(roles)
                .build();
        member.setId(2L);
        Post post = Post.builder()
                .member(wrtier)
                .image("image")
                .content("content")
                .build();
        post.setLikeCount(10);
        post.setId(2L);
        given(securityUtil.getLoginMember()).willReturn(member);
        given(postRepository.findWithMemberById(post.getId())).willReturn(Optional.of(post));
        given(postLikeRepository.findByMemberAndPost(member, post)).willReturn(Optional.ofNullable(null));
        //when
        Throwable exception = assertThrows(BusinessException.class, () -> {
            postLikeService.postunlike(post.getId());
        });
        //then
        assertEquals(exception.getMessage(), POSTLIKE_NOT_FOUND.getMessage());
        verify(alarmService, times(0)).deletePostLikeAlarm(LIKE_POST, member, post.getMember(), post);
    }
}