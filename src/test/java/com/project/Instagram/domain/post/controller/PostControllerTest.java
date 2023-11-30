package com.project.Instagram.domain.post.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.member.controller.MemberController;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.post.dto.EditPostRequest;
import com.project.Instagram.domain.post.dto.PostCreateRequest;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest {
    @Autowired
    ObjectMapper jsonMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    private PostService postService;
    // 윤영

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
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()).accept(MediaType.APPLICATION_JSON))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()).accept(MediaType.APPLICATION_JSON))
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
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_FOLLOWED_POSTS_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_FOLLOWED_POSTS_SUCCESS.getMessage()));
        verify(postService).getPostsByFollowedMembersPage(page-1,size);
    }
    // 하늘

}