package com.project.Instagram.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.post.dto.EditPostRequest;
import com.project.Instagram.domain.post.dto.PostCreateRequest;
import com.project.Instagram.domain.post.dto.PostResponse;
import com.project.Instagram.domain.post.service.PostService;
import com.project.Instagram.global.entity.PageListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    PostService postService;

    // 윤영
    @Test
    @WithMockUser
    @DisplayName("getPost() 성공")
    void getPost() throws Exception {
        // given
        Long postId = 3L;
        PostResponse postResponse = new PostResponse("testUser", "testContent", "testImage");
        given(postService.getPostResponse(postId)).willReturn(postResponse);

        // when, then
        mvc.perform(get("/post/{postId}", postId)
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_POST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_POST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.username").value("testUser"))
                .andExpect(jsonPath("$.data.content").value("testContent"))
                .andExpect(jsonPath("$.data.image").value("testImage"))
                .andDo(print());

        verify(postService).getPostResponse(eq(postId));
    }

    @Test
    @WithMockUser
    @DisplayName("userGetAllPostPage() 성공")
    void userGetAllPostPage() throws Exception {
        // given
        Long memberId = 1L;
        int page = 1;
        int size = 2;
        List<PostResponse> postResponses = Arrays.asList(
                new PostResponse("user1", "content1", "image1"),
                new PostResponse("user2", "content2", "image2")
        );
        Page<PostResponse> pageData = new PageImpl<>(postResponses, PageRequest.of(page - 1, size), postResponses.size());
        given(postService.getUserPostPage(memberId, page - 1, size)).willReturn(new PageListResponse<>(postResponses, pageData));

        // when, then
        mvc.perform(get("/post/page/{memberId}", memberId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_POST_USER_PAGE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_POST_USER_PAGE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.pageInfo.page").value(page))
                .andExpect(jsonPath("$.data.pageInfo.size").value(size))
                .andExpect(jsonPath("$.data.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.data", hasSize(postResponses.size())))
                .andExpect(jsonPath("$.data.data[0].username").value("user1"))
                .andExpect(jsonPath("$.data.data[0].content").value("content1"))
                .andExpect(jsonPath("$.data.data[0].image").value("image1"))
                .andExpect(jsonPath("$.data.data[1].username").value("user2"))
                .andExpect(jsonPath("$.data.data[1].content").value("content2"))
                .andExpect(jsonPath("$.data.data[1].image").value("image2"))
                .andDo(print());

        verify(postService).getUserPostPage(eq(memberId), eq(page - 1), eq(size));
    }

    // 동엽
    @Test
    @WithMockUser
    @DisplayName("게시글 작성")
    void createPsot() throws Exception {
        String text = "랄라라라라라라";
        String fileName = "test.txt";
        String contentType = "text/type";
        byte[] content = "Hello, exex test file.".getBytes();

        MultipartFile image= new MockMultipartFile(fileName, fileName, contentType, content);
        PostCreateRequest postCreateRequest = new PostCreateRequest(text,image);

        mvc.perform(post("/post")
                .contentType(APPLICATION_JSON)
                .with(csrf()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(POST_CREATE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(POST_CREATE_SUCCESS.getMessage()));
        verify(postService).create(any());
    }
    @Test
    @WithMockUser
    @DisplayName("게시글 수정")
    void updatePost() throws Exception {
        String text = "수정수정 임수정";
        String fileName = "filetext.txt";
        String contentType = "text/type";
        byte[] content = "Hello, exex test file.".getBytes();
        Long postId = 1L;
        MultipartFile image= new MockMultipartFile(fileName, fileName, contentType, content);
        EditPostRequest editPostRequest = new EditPostRequest(text,image);

        mvc.perform(patch("/post/{post_id}",postId)
                        .contentType(APPLICATION_JSON)
                        .with(csrf()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(UPDATE_POST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(UPDATE_POST_SUCCESS.getMessage()));
        verify(postService).updatePost(any(),anyLong());
    }
    @Test
    @WithMockUser
    @DisplayName("팔로우한 유저 게시물 조회")
    void getFollowedPostsPage() throws Exception {
        int page =1;
        int size = 5;
        List<PostResponse> response = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            response.add(new PostResponse("exex212"+i,"test12","testimage"));
        }
        Page<PostResponse> pageInfo = new PageImpl<>(response, PageRequest.of(page,size),response.size());
        PageListResponse<PostResponse> pageList = new PageListResponse<>(response,pageInfo);
        given(postService.getPostsByFollowedMembersPage(page, size)).willReturn(pageList);

        mvc.perform(get("/post/followed-posts")
                .contentType(APPLICATION_JSON)
                .with(csrf())
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_FOLLOWED_POSTS_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_FOLLOWED_POSTS_SUCCESS.getMessage()));
        verify(postService).getPostsByFollowedMembersPage(page-1,size);
    }
    // 하늘

}