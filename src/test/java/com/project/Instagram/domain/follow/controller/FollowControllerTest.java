package com.project.Instagram.domain.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.service.FollowService;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.entity.PageListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;
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

    // 하늘
}