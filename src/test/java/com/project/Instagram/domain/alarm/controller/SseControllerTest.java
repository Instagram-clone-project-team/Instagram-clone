package com.project.Instagram.domain.alarm.controller;

import com.project.Instagram.domain.alarm.service.SseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SseControllerTest.class)
@MockBean(JpaMetamodelMappingContext.class)
public class SseControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    SseService sseService;

    @Test
    @DisplayName(" sse 컨트롤러 테스트")
    @WithMockUser
    public void sseconection() throws Exception {
        String username = "ffddeeww";
        mvc.perform(get("/connection")
                .param("username",username)
                .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
