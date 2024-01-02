package com.project.Instagram.domain.search.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.search.dto.SearchDto;
import com.project.Instagram.domain.search.dto.SearchHashtagDto;
import com.project.Instagram.domain.search.dto.SearchMemberDto;
import com.project.Instagram.domain.search.service.SearchService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
class SearchControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    SearchService searchService;

    @Test
    @WithMockUser
    @DisplayName("getRecommendMembersToFollow() 테스트")
    void getRecommendMembersToFollow() throws Exception {
        // given
        Member member1 = new Member();
        member1.setId(1L);
        member1.setUsername("member1");

        Member member2 = new Member();
        member2.setId(2L);
        member2.setUsername("member2");

        List<Profile> mockProfiles = Arrays.asList(
                new Profile(member1),
                new Profile(member2)
        );

        when(searchService.getRecommendMembersToFollow()).thenReturn(mockProfiles);

        // when, then
        mvc.perform(get("/search/recommend-member")
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_RECOMMEND_MEMBER_LIST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_RECOMMEND_MEMBER_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].username").value("member1"))
                .andExpect(jsonPath("$.data[1].username").value("member2"));

        verify(searchService, times(1)).getRecommendMembersToFollow();
    }

    @Test
    @WithMockUser
    @DisplayName("addRecentSearchAndUpCount() 테스트")
    void addRecentSearchAndUpCountSuccess() throws Exception {
        // given
        String type = "Member";
        String name = "testUser";

        // when,then
        mvc.perform(post("/search/recent/add")
                        .with(csrf())
                        .param("type", type)
                        .param("name", name)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SUCCESS_PROCESSING_AFTER_SEARCH_JOIN.getStatus()))
                .andExpect(jsonPath("$.message").value(SUCCESS_PROCESSING_AFTER_SEARCH_JOIN.getMessage()));

        verify(searchService, times(1)).addRecentSearchAndUpCount(type, name);
    }

    @Test
    @WithMockUser
    @DisplayName("deleteAllRecentSearch() 테스트")
    void deleteAllRecentSearchSuccess() throws Exception {
        // when, then
        mvc.perform(delete("/search/recent/all")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(DELETE_ALL_RECENT_SEARCH_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(DELETE_ALL_RECENT_SEARCH_SUCCESS.getMessage()));

        verify(searchService, times(1)).deleteAllRecentSearch();
    }

    @Test
    @DisplayName("deleteRecentSearch 테스트")
    @WithMockUser
    void deleteRecentSearch() throws Exception {
        Long searchId = 1L;
        mvc.perform(delete("/search/{search-id}", searchId)
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(DELETE_RECENT_SEARCH_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(DELETE_RECENT_SEARCH_SUCCESS.getMessage()));
        verify(searchService).deleteRecentSearch(searchId);
    }

    @Test
    @DisplayName("getAutoHashTag 테스트")
    @WithMockUser
    void getAutoHashTag() throws Exception {
        String text = "#우리집 #얼음 #나온다 웅진 코웨이";
        mvc.perform(get("/search/auto/hashtag")
                        .with(csrf())
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HASHTAG_AUTO_COMPLETE.getStatus()))
                .andExpect(jsonPath("$.message").value(HASHTAG_AUTO_COMPLETE.getMessage()));
        verify(searchService).getAutoHashtag(text);

    }

    @Test
    @DisplayName("getTop15RecentSearchesPage 테스트")
    @WithMockUser
    void getTop15RecentSearchesPage() throws Exception {

        mvc.perform(get("/search/recent/top")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_TOP_15_RECENT_SEARCH_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_TOP_15_RECENT_SEARCH_SUCCESS.getMessage()));
        verify(searchService).getTop15RecentSearches();
    }

    @Test
    @WithMockUser
    @DisplayName("get all recent search page:success")
    void test_get_all_recent_search_page_success() throws Exception {
        //given
        int page = 1;
        int size = 5;
        List<SearchDto> data = new ArrayList<>();
        data.add(new SearchHashtagDto());
        data.add(new SearchMemberDto());
        data.add(new SearchHashtagDto());
        Page<SearchDto> pageInfo = new PageImpl<>(data, PageRequest.of(page - 1, size), data.size());
        PageListResponse<SearchDto> pageList = new PageListResponse(data, pageInfo);
        when(searchService.getRecentSearchPageList(Mockito.anyInt(), Mockito.anyInt())).thenReturn(pageList);
        //when, then
        mvc.perform(get("/search/recent/all")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_RECENTSEARCH_LIST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_RECENTSEARCH_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.pageInfo.page").value(page))
                .andExpect(jsonPath("$.data.pageInfo.size").value(size))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value("3"));
        verify(searchService, atLeastOnce()).getRecentSearchPageList(page - 1, size);
    }

    @Test
    @WithMockUser
    @DisplayName("get auto member:success")
    void test_get_auto_member() throws Exception {
        //given
        String text = "luee";
        Member member1 = new Member();
        member1.setUsername(text);
        Member member2 = new Member();
        Member member3 = new Member();
        List<Profile> response = new ArrayList<>();
        response.add(new Profile(member1));
        response.add(new Profile(member2));
        response.add(new Profile(member3));
        when(searchService.getAutoMember(Mockito.anyString())).thenReturn(response);
        //when, then
        mvc.perform(get("/search/auto/member")
                        .with(csrf())
                        .param("text", Mockito.anyString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(MEMBER_AUTO_COMPLETE.getStatus()))
                .andExpect(jsonPath("$.message").value(MEMBER_AUTO_COMPLETE.getMessage()))
                .andExpect(jsonPath("$.data[0].username").value(text));
        verify(searchService, atLeastOnce()).getAutoMember(Mockito.anyString());
    }

    @Test
    @WithMockUser
    @DisplayName("search:success")
    void test_search_success() throws Exception {
        //given
        List<SearchDto> response = new ArrayList<>();
        response.add(new SearchHashtagDto());
        response.add(new SearchMemberDto());
        response.add(new SearchHashtagDto());
        when(searchService.searchByText(Mockito.anyString())).thenReturn(response);
        //when, then
        mvc.perform(get("/search")
                        .with(csrf())
                        .param("text", Mockito.anyString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SEARCH_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(SEARCH_SUCCESS.getMessage()));
        verify(searchService, atLeastOnce()).searchByText(Mockito.anyString());
    }
}