package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.follow.service.FollowService;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.MemberRole;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.mention.service.MentionService;
import com.project.Instagram.domain.post.dto.PostResponse;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.S3Uploader;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
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

import java.time.LocalDateTime;
import java.util.*;

import static com.project.Instagram.global.error.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    PostService postService;

    @Mock
    SecurityUtil securityUtil;
    @Mock
    PostRepository postRepository;
    @Mock
    HashtagService hashtagService;
    @Mock
    S3Uploader s3Uploader;
    @Mock
    FollowService followService;
    @Mock
    MentionService mentionService;
    @Mock
    AlarmService alarmService;

    // 윤영

    // 동엽

    // 하늘
    @Test
    @DisplayName("get post page list:success")
    void test_get_post_page_list(){
        //given
        Set<MemberRole> roles=new HashSet<>();
        roles.add(MemberRole.USER);
        Member member=Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .roles(roles)
                .build();

        List<Post> posts = new ArrayList<>();
        for(int n=1; n<=10; n++){
            Post post=Post.builder()
                    .member(member)
                    .image("image"+n)
                    .content("content"+n)
                    .build();
            posts.add(post);
        }
        int page = 0;
        int size = 2;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> postPages = new PageImpl<>(posts, pageRequest, posts.size());
        given(postRepository.findAllPostPage(pageRequest)).willReturn(postPages);

        //when
        PageListResponse<PostResponse> response = postService.getPostPageList(page, size);

        //then
        assertNotNull(response);
        assertEquals(size, response.getPageInfo().getSize());
        assertEquals(posts.size(), response.getPageInfo().getTotalElements());
        assertEquals(posts.get(0).getMember().getUsername(), response.getData().get(0).getUsername());
        assertEquals(posts.get(1).getImage(), response.getData().get(1).getImage());
        assertEquals(page, response.getPageInfo().getPage() - 1);
        verify(postRepository, times(1)).findAllPostPage(pageRequest);
    }

    @Test
    @DisplayName("get my post page:success")
    void test_get_my_post_page(){
        //given
        Set<MemberRole> roles=new HashSet<>();
        roles.add(MemberRole.USER);
        Member member=Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .roles(roles)
                .build();
        member.setId(1L);

        List<Post> posts = new ArrayList<>();
        for(int n=1; n<=10; n++){
            Post post=Post.builder()
                    .member(member)
                    .image("image"+n)
                    .content("content"+n)
                    .build();
            posts.add(post);
        }
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPages = new PageImpl<>(posts, pageable, posts.size());
        given(postRepository.findMemberAllPostPage(member.getId(), pageable)).willReturn(postPages);
        given(securityUtil.getLoginMember()).willReturn(member);
        //when
        PageListResponse<PostResponse> response = postService.getMyPostPage(page, size);

        //then
        assertNotNull(response);
        assertEquals(size, response.getPageInfo().getSize());
        assertEquals(posts.size(), response.getPageInfo().getTotalElements());
        assertEquals(posts.get(0).getMember().getUsername(), response.getData().get(0).getUsername());
        assertEquals(posts.get(1).getImage(), response.getData().get(1).getImage());
        assertEquals(page, response.getPageInfo().getPage() - 1);
        verify(postRepository, times(1)).findMemberAllPostPage(member.getId(), pageable);
    }

    @Test
    @DisplayName("delete post:success")
    void test_delete_post_success(){
        //given
        Set<MemberRole> roles=new HashSet<>();
        roles.add(MemberRole.USER);
        Member member=Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .roles(roles)
                .build();
        member.setId(1L);
        Post post=Post.builder()
                .member(member)
                .image("image")
                .content("content")
                .build();
        post.setId(2L);
        given(securityUtil.getLoginMember()).willReturn(member);
        given(postRepository.findWithMemberById(post.getId())).willReturn(Optional.of(post));
        //when
        postService.delete(post.getId());
        //then
        assertNotNull(post.getDeletedAt());
        verify(alarmService, times(1)).deleteAllPostAlarm(post);
    }
    @Test
    @DisplayName("delete post:[exception]post already deleted")
    void test_delete_post_throw_post_already_deleted(){
        //given
        Set<MemberRole> roles=new HashSet<>();
        roles.add(MemberRole.USER);
        Member member=Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .roles(roles)
                .build();
        member.setId(1L);
        Post post=Post.builder()
                .member(member)
                .image("image")
                .content("content")
                .build();
        post.setId(2L);
        post.setDeletedAt(LocalDateTime.now());
        given(securityUtil.getLoginMember()).willReturn(member);
        given(postRepository.findWithMemberById(post.getId())).willReturn(Optional.of(post));
        //when
        Throwable exception = assertThrows(BusinessException.class, () -> {
            postService.delete(post.getId());
        });
        //then
        assertEquals(exception.getMessage(), POST_ALREADY_DELETED.getMessage());
        verify(alarmService, times(0)).deleteAllPostAlarm(post);
    }

    @Test
    @DisplayName("delete post:[exception]post delete failed")
    void test_delete_post_throw_post_delete_failed(){
        //given
        Set<MemberRole> roles=new HashSet<>();
        roles.add(MemberRole.USER);
        Member member=Member.builder()
                .username("luee")
                .name("haneul")
                .email("haha@gmail.com")
                .password("pwd12345678")
                .roles(roles)
                .build();
        member.setId(1L);
        Member anotherMember=Member.builder()
                .username("luee2")
                .name("haneul2")
                .email("haha2@gmail.com")
                .password("pwd123456789")
                .roles(roles)
                .build();
        anotherMember.setId(2L);
        Post post=Post.builder()
                .member(anotherMember)
                .image("image")
                .content("content")
                .build();
        post.setId(2L);
        post.setDeletedAt(LocalDateTime.now());
        given(securityUtil.getLoginMember()).willReturn(member);
        given(postRepository.findWithMemberById(post.getId())).willReturn(Optional.of(post));
        //when
        Throwable exception = assertThrows(BusinessException.class, () -> {
            postService.delete(post.getId());
        });
        //then
        assertEquals(exception.getMessage(), POST_DELETE_FAILED.getMessage());
        verify(alarmService, times(0)).deleteAllPostAlarm(post);
    }
}