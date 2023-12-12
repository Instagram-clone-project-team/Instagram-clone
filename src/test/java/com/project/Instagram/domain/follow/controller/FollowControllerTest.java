package com.project.Instagram.domain.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.service.FollowService;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.global.entity.PageListResponse;
import org.assertj.core.api.Assertions;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.FOLLOWERS_LIST_SUCCESS;
import static com.project.Instagram.global.response.ResultCode.FOLLOWER_COUNT_SUCCESS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FollowController.class)
@MockBean(JpaMetamodelMappingContext.class)
class FollowControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    FollowService followService;

    // 윤영

    // 동엽

    // 하늘
    @Test
    @WithMockUser
    @DisplayName("get followers page:success")
    void test_get_followers_page() throws Exception{
        String memberUsername = "luee";

        Member member1 = new Member();
        Member member2 = new Member();
        Member member3 = new Member();
        List<FollowerDto> data = new ArrayList<>();
        data.add(new FollowerDto(member1, true, true, false));
        data.add(new FollowerDto(member2, true, true, false));
        data.add(new FollowerDto(member3, true, true, false));
        Page<FollowerDto> pageInfo = new PageImpl<>(data, PageRequest.of(0, 2), data.size());
        PageListResponse<FollowerDto> pageList = new PageListResponse(data, pageInfo);

        when(followService.getFollowers(eq(memberUsername), Mockito.anyInt(), Mockito.anyInt())).thenReturn(pageList);
        mvc.perform(get("/follow/followers/{memberUsername}", memberUsername)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(FOLLOWERS_LIST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(FOLLOWERS_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.pageInfo.page").value("1"))
                .andExpect(jsonPath("$.data.pageInfo.size").value("2"))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value("3"))
                .andDo(print());

        verify(followService, atLeastOnce()).getFollowers(eq(memberUsername), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @WithMockUser
    @DisplayName("get follower count:success")
    void test_get_follower_count() throws Exception {
        String memberUsername = "luee";
        int count = 10;
        when(followService.getFollowerCount(memberUsername)).thenReturn(count);
        mvc.perform(get("/follow/follower-count/{memberUsername}", memberUsername)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(FOLLOWER_COUNT_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(FOLLOWER_COUNT_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").value(count))
                .andDo(print());

        verify(followService, atLeastOnce()).getFollowerCount(memberUsername);
    }

    @Test
    @WithMockUser
    @DisplayName("get follower count:fail")
    void test_get_follower_count_throw_exception() throws Exception {
        //생각대로 오류문구가 출력이 안됨. " "이걸 입력하면 서비스 로직 에러가 뜨고, ""입력하면 405가 뜸.
        //사용자 이름이 필요합니다.가 뜨는 조건을 모르겠음.
        //" " vs ""
        String memberUsername = " ";
        int count = 10;
        //when(followService.getFollowingCount(memberUsername)).thenReturn(count);
        given(followService.getFollowerCount(memberUsername)).willReturn(count);
//        mvc.perform(get("/follow/follower-count/{memberUsername}", memberUsername)
//                        .with(csrf()))
//                //.andExpect(model().errorCount(1))
//                .andExpect(status().isInternalServerError());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/follow/follower-count/{memberUsername}", memberUsername)
                .with(csrf())).andExpect(status().isOk())).hasCause(new ConstraintViolationException("getFollowerCount.memberUsername: 사용자 이름이 필요합니다.", null));
        //String message = result.exce
        //Assertions.assertThat(message).contains("사용자 이름이 필요합니다.");
       // verify(followService, atLeastOnce()).getFollowerCount(memberUsername);
    }
}