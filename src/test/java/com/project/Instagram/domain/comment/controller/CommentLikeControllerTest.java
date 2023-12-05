package com.project.Instagram.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.comment.service.CommentLikeService;
import com.project.Instagram.domain.member.dto.LikesMemberResponseDto;
import com.project.Instagram.domain.member.service.MemberService;
import com.project.Instagram.global.entity.PageListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentLikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CommentLikeControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    CommentLikeService commentLikeService;
    @MockBean
    MemberService memberService;

    @Test
    @WithMockUser
    @DisplayName("commentUnLike() 테스트")
    void commentUnLike() throws Exception {
        // given
        Long commentId = 1L;

        // when
        doNothing().when(commentLikeService).DeleteCommentLike(commentId);

        // then
        mvc.perform(delete("/commentlike")
                        .param("commentId", String.valueOf(commentId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_UNLIKE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_UNLIKE_SUCCESS.getMessage()));

        verify(commentLikeService, times(1)).DeleteCommentLike(commentId);
    }

    @Test
    @WithMockUser
    @DisplayName("getCommentLikeUserPage() 테스트")
    void getCommentLikeUserPage() throws Exception{
        // given
        Long commentId = 1L;
        int page = 1;
        int size = 5;

        List<LikesMemberResponseDto> likesMemberResponseDtos = new ArrayList<>();
        PageListResponse<LikesMemberResponseDto> pageListResponse = new PageListResponse<>(likesMemberResponseDtos, new PageImpl(likesMemberResponseDtos));

        when(commentLikeService.getCommentLikeUsers(commentId, page - 1, size)).thenReturn(pageListResponse);

        // when, then
        mvc.perform(get("/commentlike/{commentId}", commentId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_POSTLIKE_USERS_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_POSTLIKE_USERS_SUCCESS.getMessage()));

        verify(commentLikeService, times(1)).getCommentLikeUsers(commentId, page - 1, size);
    }

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