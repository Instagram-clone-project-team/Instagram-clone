package com.project.Instagram.domain.comment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.comment.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static com.project.Instagram.global.response.ResultCode.SIGNUP_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    // 하늘
    @Test
    @DisplayName("create comment :success")
    @WithMockUser
    void test_create_comment() throws Exception {
        //given
        Map<String, Object> map=new HashMap<>();
        map.put("text", "댓글");
        map.put("postId", 1L);
        //CommentRequest가 Getter만 있는 DTO. 값을 넣을 수 있는 생성자가 없어서 Map으로 만듬.

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