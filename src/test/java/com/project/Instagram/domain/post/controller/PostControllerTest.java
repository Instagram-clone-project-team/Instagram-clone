package com.project.Instagram.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.member.controller.MemberController;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.member.service.MemberService;
import com.project.Instagram.domain.post.dto.PostResponse;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest {

    @Autowired
    ObjectMapper jsonMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    PostService postService;

    // 윤영

    // 동엽

    // 하늘
    @Test
    @WithMockUser
    @DisplayName("get all post page:success")
    void test_get_all_post_page() throws Exception {
        //given
        List<PostResponse> data = new ArrayList<>();
        data.add(new PostResponse());
        data.add(new PostResponse());
        data.add(new PostResponse());
        Page<PostResponse> pageInfo = new PageImpl<>(data, PageRequest.of(0, 2), data.size());
        PageListResponse<PostResponse> pageList = new PageListResponse(data, pageInfo);
        given(postService.getPostPageList(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageList);

        //when, then
        mvc.perform(get("/post/allpost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(GET_POST_PAGE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_POST_PAGE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.pageInfo.page").value("1"))
                .andExpect(jsonPath("$.data.pageInfo.size").value("2"))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value("3"))
                .andDo(print());

        verify(postService).getPostPageList(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @WithMockUser
    @DisplayName("get my post page:success")
    void test_get_my_post_page() throws Exception {
        List<PostResponse> data = new ArrayList<>();
        data.add(new PostResponse());
        data.add(new PostResponse());
        data.add(new PostResponse());
        Page<PostResponse> pageInfo = new PageImpl<>(data, PageRequest.of(0, 2), data.size());
        PageListResponse<PostResponse> pageList = new PageListResponse(data, pageInfo);
        given(postService.getMyPostPage(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageList);

        //when, then
        mvc.perform(get("/post/myposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(GET_POST_PAGE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_POST_PAGE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.pageInfo.page").value("1"))
                .andExpect(jsonPath("$.data.pageInfo.size").value("2"))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value("3"))
                .andDo(print());

        verify(postService).getMyPostPage(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @WithMockUser
    @DisplayName("get delete post:success")
    void test_delete_post() throws Exception {

        //when, then
        mvc.perform(delete("/post/{post_id}", Mockito.anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(DELETE_POST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(DELETE_POST_SUCCESS.getMessage()))
                .andDo(print());

        verify(postService).delete(Mockito.anyLong());
    }
}