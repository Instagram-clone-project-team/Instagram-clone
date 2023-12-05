package com.project.Instagram.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.post.dto.PostResponse;
import com.project.Instagram.domain.post.service.PostLikeService;
import com.project.Instagram.domain.post.service.PostService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.GET_POST_PAGE_SUCCESS;
import static com.project.Instagram.global.response.ResultCode.POST_UNLIKE_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PostLikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostLikeControllerTest {

    @Autowired
    ObjectMapper jsonMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    PostLikeService postLikeService;

    // 윤영

    // 동엽

    // 하늘
    @Test
    @WithMockUser
    @DisplayName("post unlike:success")
    void test_post_unlike() throws Exception {
        //given

        //when, then
        mvc.perform(delete("/postlike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("postId", "1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(POST_UNLIKE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(POST_UNLIKE_SUCCESS.getMessage()))
                .andDo(print());

        verify(postLikeService).postunlike(Mockito.anyLong());
    }
}