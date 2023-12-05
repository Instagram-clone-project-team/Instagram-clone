package com.project.Instagram.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.comment.dto.CommentRequest;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.service.CommentService;
import com.project.Instagram.domain.post.entity.Post;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    // 동엽
    @DisplayName("replyComment test")
    @Test
    @WithMockUser
    void replyCommentTest() throws Exception {
        String text = "ttteesstt";
        Post post = new Post();
        post.setId(1L);
        Comment comment = new Comment();
        comment.setId(1L);
        CommentRequest commentRequest = new CommentRequest(text,post.getId(),comment.getId());
        mvc.perform(post("/comment/reply")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(commentRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_REPLY_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_REPLY_SUCCESS.getMessage()));

        verify(commentService).createReplyComment(anyString(),anyLong(),anyLong());
    }
    @DisplayName("updateComment test")
    @Test
    @WithMockUser
    void updateCommentTest() throws Exception {
        String text = "ttteesstt";
        Post post = new Post();
        post.setId(1L);
        Comment comment = new Comment();
        comment.setId(1L);
        CommentRequest commentRequest = new CommentRequest(text,post.getId(),comment.getId());
        mvc.perform(patch("/comment/{comment-id}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_UPDATE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_UPDATE_SUCCESS.getMessage()));

        verify(commentService).updateComment(anyLong(),anyString());
    }
    @DisplayName("delete comment test")
    @Test
    @WithMockUser
    void deleteCommentTest() throws Exception {
        String text = "ttteesstt";
        Comment comment = new Comment();
        comment.setId(1L);
        mvc.perform(delete("/comment/{comment-id}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_DELETE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_DELETE_SUCCESS.getMessage()));

        verify(commentService).deleteComment(anyLong());
    }
    // 하늘

}