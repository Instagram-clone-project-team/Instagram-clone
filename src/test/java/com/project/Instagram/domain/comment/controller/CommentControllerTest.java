package com.project.Instagram.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.comment.dto.CommentResponse;
import com.project.Instagram.domain.comment.dto.SimpleComment;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.service.CommentService;
import com.project.Instagram.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.project.Instagram.global.response.ResultCode.COMMENT_GET_SUCCESS;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CommentControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    CommentService commentService;

    // 윤영
    @Test
    @WithMockUser
    @DisplayName("getComments() 테스트")
    void getComments() throws Exception {
        // given
        Long postId = 1L;

        Member member = Member.builder()
                .username("testUsername")
                .email("test122@gmail.com")
                .build();

        Comment comment = Comment.builder()
                .writer(member)
                .postId(postId)
                .text("testText")
                .parentsCommentId(1L)
                .replyOrder(1)
                .build();

        SimpleComment simpleComment = SimpleComment.builder()
                .comment(comment)
                .build();

        CommentResponse commentResponse = CommentResponse.builder()
                .comment(simpleComment)
                .replies(List.of(simpleComment))
                .build();

        // when
        when(commentService.getCommentsByPostId(postId)).thenReturn(List.of(commentResponse));

        // then
        mvc.perform(get("/comment/{post-id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_GET_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_GET_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data[0].comment.username").value(simpleComment.getUsername()))
                .andExpect(jsonPath("$.data[0].comment.image").value(simpleComment.getImage()))
                .andExpect(jsonPath("$.data[0].comment.text").value(simpleComment.getText()))
                .andExpect(jsonPath("$.data[0].replies").isNotEmpty());

        verify(commentService, times(1)).getCommentsByPostId(postId);
        // 동엽

        // 하늘
    }
}