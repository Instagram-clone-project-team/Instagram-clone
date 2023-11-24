package com.project.Instagram.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.member.controller.MemberController;
import com.project.Instagram.domain.member.dto.SendPasswordEmailRequest;
import com.project.Instagram.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("비밀번호인증코드 이메일 보내기 테스트")
    @WithMockUser
    public void sendPasswordCodeByEmailTest() throws Exception{
        SendPasswordEmailRequest sendPasswordEmailRequest = new SendPasswordEmailRequest("exex22");

        mockMvc.perform(
                post("/password/reset/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(sendPasswordEmailRequest)))
                .andExpect(status().isOk());

        verify(memberService).sendPasswordCodeEmail(sendPasswordEmailRequest);
    }
    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser
    public void logoutTest() throws Exception{
        mockMvc.perform(
                delete("/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(LOGOUT_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(LOGOUT_SUCCESS.getMessage()));
        verify(memberService).logout();
    }
    @Test
    @DisplayName("프로필 가져오기 테스트")
    @WithMockUser
    public void getProfileTest() throws Exception{
        String username = "exex22";

        mockMvc.perform(
                get("/member/{username}",username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GET_PROFILE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(GET_PROFILE_SUCCESS.getMessage()));


        verify(memberService).getProfile(username);
    }
    @Test
    @DisplayName("토큰 재발급 테스트")
    @WithMockUser
    public void reissueRefreshTokenTest() throws Exception {
        String refreshToken = "ex_refresh_token";
        String Authorization = "ex_access_token";

        mockMvc.perform(
                post("/token/reissue")
                        .with(csrf())
                        .header("refresh", refreshToken)
                        .header("Authorization", Authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(REISSUE_JWT_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(REISSUE_JWT_SUCCESS.getMessage()));

        verify(memberService).reissueAccessToken(Authorization,refreshToken);
    }
}
