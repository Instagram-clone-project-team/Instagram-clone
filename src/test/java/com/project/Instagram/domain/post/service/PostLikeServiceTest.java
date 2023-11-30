package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.member.entity.Member;
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

import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.POSTLIKE_ALREADY_EXIST;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void postlike(){
        Member member = new Member();
        member.setId(1L);
        Member member2 = new Member();
        Post post = new Post();
        post.setMember(member2);
        post.setLikeCount(0);

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(postRepository.findWithMemberById(post.getId())).thenReturn(Optional.of(post));

        postLikeService.postlike(post.getId());

        verify(postLikeRepository).findByMemberAndPost(any(),any());
        verify(postLikeRepository).save(any());
        verify(alarmService).sendPostLikeAlarm(any(),any(),any(),any());
    }
    @Test
    @DisplayName("이미 좋아요 한 상태에서 좋아요 생성")
    void postlikesituationexist(){
        Member member = new Member();
        member.setId(1L);
        Member member2 = new Member();
        Post post = new Post();
        post.setMember(member2);
        post.setLikeCount(0);
        PostLike postLike=new PostLike(member,post);
        when(securityUtil.getLoginMember()).thenReturn(member);
        when(postRepository.findWithMemberById(post.getId())).thenReturn(Optional.of(post));
        when(postLikeRepository.findByMemberAndPost(member,post)).thenReturn(Optional.of(postLike));


        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(()->postLikeService.postlike(post.getId()))
                .withMessage(POSTLIKE_ALREADY_EXIST.getMessage());
    }
    // 하늘

}