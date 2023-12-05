package com.project.Instagram.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.comment.service.CommentLikeService;
import com.project.Instagram.domain.member.controller.MemberController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static com.project.Instagram.global.response.ResultCode.COMMENT_CREATE_SUCCESS;
import static com.project.Instagram.global.response.ResultCode.COMMENT_LIKE_SUCCESS;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CommentLikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CommentLikeControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    CommentLikeService commentLikeService;

    // 윤영

    // 동엽

    // 하늘
    @Test
    @DisplayName("create comment like :success")
    @WithMockUser
    void test_create_comment_like() throws Exception {
        //given
        Long commentId = 1L;

        //when, then
        mvc.perform(post("/commentlike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("commentId", String.valueOf(commentId))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_LIKE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_LIKE_SUCCESS.getMessage()))
                .andDo(print());

        verify(commentLikeService).createCommentLike(commentId);
    }
}