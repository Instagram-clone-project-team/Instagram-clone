package com.project.Instagram.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.member.dto.LikesMemberResponseDto;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.service.PostLikeService;
import com.project.Instagram.global.entity.PageListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.project.Instagram.global.response.ResultCode.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    @WithMockUser
    @DisplayName("getThePostPostLikeUserPage() 테스트")
    void getThePostPostLikeUserPage() throws Exception {
        // given
        Long postId = 1L;
        int page = 1;
        int size = 5;

        Member member = new Member();
        member.setUsername("testUsername");
        member.setImage("testImage");
        member.setIntroduce("testIntroduce");

        LikesMemberResponseDto likesMemberResponseDto = new LikesMemberResponseDto(member);
        PageListResponse<LikesMemberResponseDto> pageListResponse = new PageListResponse<>(singletonList(likesMemberResponseDto), new PageImpl<>(emptyList()));

        when(postLikeService.getPostLikeUsers(postId, page - 1, size)).thenReturn(pageListResponse);

        // when, then
        mvc.perform(get("/postlike/{postId}", postId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(jsonMapper.writeValueAsString(postId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_POSTLIKE_USERS_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_POSTLIKE_USERS_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.data[0].profile.username").value("testUsername"))
                .andExpect(jsonPath("$.data.data[0].profile.image").value("testImage"))
                .andExpect(jsonPath("$.data.data[0].profile.introduce").value("testIntroduce"));

        verify(postLikeService, times(1)).getPostLikeUsers(postId, page - 1, size);
    }

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

    @Test
    @WithMockUser
    @DisplayName("post unlike:success")
    void test_post_unlike() throws Exception {
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