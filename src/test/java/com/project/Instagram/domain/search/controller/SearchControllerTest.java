package com.project.Instagram.domain.search.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.search.dto.SearchDto;
import com.project.Instagram.domain.search.dto.SearchMemberDto;
import com.project.Instagram.domain.search.service.SearchService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    // 윤영

    // 동엽
    @Test
    @DisplayName("deleteRecentSearch 테스트")
    @WithMockUser
    void deleteRecentSearch() throws Exception {
        Long searchId =1L;
        mvc.perform(delete("/search/{search-id}",searchId)
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
                .param("text",text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HASHTAG_AUTO_COMPLETE.getStatus()))
                .andExpect(jsonPath("$.message").value(HASHTAG_AUTO_COMPLETE.getMessage()));
        verify(searchService).getAutoHashtag(text);

    }
    @Test
    @DisplayName("getTop15RecentSearchesPage 테스트")
    @WithMockUser
    void getTop15RecentSearchesPage() throws Exception{

        mvc.perform(get("/search/recent/top")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_TOP_15_RECENT_SEARCH_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_TOP_15_RECENT_SEARCH_SUCCESS.getMessage()));
        verify(searchService).getTop15RecentSearches();
    }
    // 하늘
}