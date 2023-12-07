package com.project.Instagram.domain.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.follow.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

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
}