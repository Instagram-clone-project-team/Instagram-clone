package com.project.Instagram.domain.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.follow.service.FollowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    // 동엽
    //follow. getFollowingCount
    @Test
    @DisplayName("follow() 성공")
    @WithMockUser
    void followSuccess() throws Exception {
        String followMemberUsername = "exex22";

        when(followService.follow(followMemberUsername)).thenReturn(true);
        mvc.perform(post("/follow/{followMemberUsername}",followMemberUsername)
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
        mvc.perform(post("/follow/{followMemberUsername}",followMemberUsername)
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

        mvc.perform(get("/follow/following-count/{memberUsername}",memberUsername)
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
}