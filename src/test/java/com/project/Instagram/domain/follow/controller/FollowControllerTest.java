package com.project.Instagram.domain.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.service.FollowService;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.entity.PageListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowController.class)
@MockBean(JpaMetamodelMappingContext.class)
class FollowControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    FollowService followService;

    // 윤영
    @Test
    @WithMockUser
    @DisplayName("unfollow() 테스트")
    void unfollow() throws Exception {
        // given
        String followMemberUsername = "HeoCoding";

        // when
        when(followService.unfollow(followMemberUsername)).thenReturn(true);

        // then
        mvc.perform(delete("/follow/{followMemberUsername}", followMemberUsername)
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(UNFOLLOW_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(UNFOLLOW_SUCCESS.getMessage()));

        verify(followService).unfollow(followMemberUsername);
    }

    @Test
    @WithMockUser
    @DisplayName("getFollowingsPage() 테스트")
    void getFollowingsPage() throws Exception {
        // given
        String memberUsername = "HeoCoding";
        int page = 1;
        int size = 5;

        Member member = Member.builder()
                .username("HeoCoding")
                .build();

        FollowerDto followerDto = new FollowerDto(member, true, false, false);
        List<FollowerDto> followerDtos = Arrays.asList(followerDto);
        PageListResponse<FollowerDto> pageListResponse = new PageListResponse<>(followerDtos, new PageImpl<>(followerDtos, PageRequest.of(page - 1, size), followerDtos.size()));

        // when
        when(followService.getFollowings(memberUsername, page - 1, size)).thenReturn(pageListResponse);

        // then
        mvc.perform(get("/follow/following/{memberUsername}", memberUsername)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(FOLLOWINGS_LIST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(FOLLOWINGS_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.pageInfo.page").value(page))
                .andExpect(jsonPath("$.data.pageInfo.size").value(size))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value(followerDtos.size()))
                .andExpect(jsonPath("$.data.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.data.data[0].member.username").value(member.getUsername()))
                .andExpect(jsonPath("$.data.data[0].following").value(true))
                .andExpect(jsonPath("$.data.data[0].follower").value(false))
                .andExpect(jsonPath("$.data.data[0].me").value(false));

        verify(followService).getFollowings(memberUsername, page - 1, size);
    }

    // 동엽
    //follow. getFollowingCount
    @Test
    @DisplayName("follow() 성공")
    @WithMockUser
    void followSuccess() throws Exception {
        String followMemberUsername = "exex22";

        when(followService.follow(followMemberUsername)).thenReturn(true);
        mvc.perform(post("/follow/{followMemberUsername}", followMemberUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.status").value(FOLLOW_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(FOLLOW_SUCCESS.getMessage()));
        verify(followService).follow(followMemberUsername);
    }

    @Test
    @DisplayName("follow() 실패")
    @WithMockUser
    void followFail() throws Exception {
        String followMemberUsername = "exex22";

        when(followService.follow(followMemberUsername)).thenReturn(false);
        mvc.perform(post("/follow/{followMemberUsername}", followMemberUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false))
                .andExpect(jsonPath("$.status").value(FOLLOW_FAIL.getStatus()))
                .andExpect(jsonPath("$.message").value(FOLLOW_FAIL.getMessage()));
        verify(followService).follow(followMemberUsername);
    }

    @Test
    @DisplayName("getFollowingCount")
    @WithMockUser
    void getFollowingCount() throws Exception {
        String memberUsername = "exex22";

        mvc.perform(get("/follow/following-count/{memberUsername}", memberUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.status").value(FOLLOWING_COUNT_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(FOLLOWING_COUNT_SUCCESS.getMessage()));
        verify(followService).getFollowingCount(memberUsername);
    }

    // 하늘
    @Test
    @WithMockUser
    @DisplayName("get followers page:success")
    void test_get_followers_page() throws Exception {
        String memberUsername = "luee";

        Member member1 = new Member();
        Member member2 = new Member();
        Member member3 = new Member();
        List<FollowerDto> data = new ArrayList<>();
        data.add(new FollowerDto(member1, true, true, false));
        data.add(new FollowerDto(member2, true, true, false));
        data.add(new FollowerDto(member3, true, true, false));
        Page<FollowerDto> pageInfo = new PageImpl<>(data, PageRequest.of(0, 2), data.size());
        PageListResponse<FollowerDto> pageList = new PageListResponse(data, pageInfo);

        when(followService.getFollowers(eq(memberUsername), Mockito.anyInt(), Mockito.anyInt())).thenReturn(pageList);
        mvc.perform(get("/follow/followers/{memberUsername}", memberUsername)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(FOLLOWERS_LIST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(FOLLOWERS_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.pageInfo.page").value("1"))
                .andExpect(jsonPath("$.data.pageInfo.size").value("2"))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value("3"));

        verify(followService, atLeastOnce()).getFollowers(eq(memberUsername), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @WithMockUser
    @DisplayName("get follower count:success")
    void test_get_follower_count() throws Exception {
        String memberUsername = "luee";
        int count = 10;
        when(followService.getFollowerCount(memberUsername)).thenReturn(count);
        mvc.perform(get("/follow/follower-count/{memberUsername}", memberUsername)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(FOLLOWER_COUNT_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(FOLLOWER_COUNT_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").value(count));

        verify(followService, atLeastOnce()).getFollowerCount(memberUsername);
    }

    @Test
    @WithMockUser
    @DisplayName("get follower count:fail")
    void test_get_follower_count_throw_exception() throws Exception {

        String memberUsername = " ";
        int count = 10;
        when(followService.getFollowingCount(memberUsername)).thenReturn(count);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/follow/follower-count/{memberUsername}", memberUsername)
                .with(csrf())).andExpect(status().isOk())).hasCause(new ConstraintViolationException("getFollowerCount.memberUsername: 사용자 이름이 필요합니다.", null));

    }
}