package com.project.Instagram.domain.comment.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.comment.dto.CommentRequest;
import com.project.Instagram.domain.comment.dto.CommentResponse;
import com.project.Instagram.domain.comment.dto.SimpleComment;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.service.CommentService;
import com.project.Instagram.domain.member.entity.Member;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    }

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
        CommentRequest commentRequest = new CommentRequest(text, post.getId(), comment.getId());
        mvc.perform(post("/comment/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_REPLY_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_REPLY_SUCCESS.getMessage()));

        verify(commentService).createReplyComment(anyString(), anyLong(), anyLong());
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
        CommentRequest commentRequest = new CommentRequest(text, post.getId(), comment.getId());
        mvc.perform(patch("/comment/{comment-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_UPDATE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_UPDATE_SUCCESS.getMessage()));
    }

    @DisplayName("delete comment test")
    @Test
    @WithMockUser
    void deleteCommentTest() throws Exception {
        String text = "ttteesstt";
        Comment comment = new Comment();
        comment.setId(1L);
        mvc.perform(delete("/comment/{comment-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_DELETE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_DELETE_SUCCESS.getMessage()));

        verify(commentService).deleteComment(anyLong());
    }

    // 하늘
    @Test
    @DisplayName("create comment :success")
    @WithMockUser
    void test_create_comment() throws Exception {
        //given
        Map<String, Object> map = new HashMap<>();
        map.put("text", "댓글");
        map.put("postId", 1L);

        //when, then
        mvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(COMMENT_CREATE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(COMMENT_CREATE_SUCCESS.getMessage()))
                .andDo(print());

        verify(commentService).createComment(map.get("text").toString(), Long.valueOf(map.get("postId").toString()));
    }
}