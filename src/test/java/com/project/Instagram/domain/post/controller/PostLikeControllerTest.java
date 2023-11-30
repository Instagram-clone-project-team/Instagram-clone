package com.project.Instagram.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.post.service.PostLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.Instagram.global.response.ResultCode.POST_CREATE_SUCCESS;
import static com.project.Instagram.global.response.ResultCode.POST_LIKE_SUCCESS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostLikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostLikeControllerTest {
    @Autowired
    ObjectMapper jsonMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    private PostLikeService postLikeService;

    // 윤영

    // 동엽
    @Test
    @WithMockUser
    @DisplayName("좋아요 생성")
    void postLike() throws Exception {
        Long postId = 1L;
        mvc.perform(post("/postlike")
                .param("postId", String.valueOf(postId))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(POST_LIKE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(POST_LIKE_SUCCESS.getMessage()));

        verify(postLikeService, times(1)).postlike(postId);
    }
    // 하늘

}