package com.project.Instagram.domain.search.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

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

    // 하늘
}