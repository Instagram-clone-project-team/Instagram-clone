package com.project.Instagram.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.comment.service.CommentLikeService;
import com.project.Instagram.domain.member.controller.MemberController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CommentLikeControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    CommentLikeService commentLikeService;

    // 윤영

    // 동엽

    // 하늘

}