package com.project.Instagram.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.member.dto.ResetPasswordRequest;
import com.project.Instagram.domain.member.dto.UpdatePasswordRequest;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(MemberController.class)
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;

    @Test
    @WithMockUser
    @DisplayName("이메일 인증 코드 앤드포인트 동작 테스트")
    void sendAuthCodeByEmail() throws Exception {
        // given
        String email = "test@example.com";

        doNothing().when(memberService).sendAuthEmail(email);

        // when, then
        mockMvc.perform(post("/accounts/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SEND_EMAIL_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(SEND_EMAIL_SUCCESS.getMessage()));

        verify(memberService, times(1)).sendAuthEmail(email);
    }

    @Test
    @WithMockUser
    @DisplayName("비밀번호 변경 엔드포인트 동작 테스트")
    void updatePassword() throws Exception {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "ValidPassword123");

        // When, then
        mockMvc.perform(patch("/password/patch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(UPDATE_PASSWORD_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(UPDATE_PASSWORD_SUCCESS.getMessage()));

        verify(memberService, times(1)).updatePassword(request);
    }

    @Test
    @WithMockUser
    @DisplayName("비밀번호 리셋 엔드포인트 동작 테스트")
    void resetPassword() throws Exception {
        // given
        ResetPasswordRequest request = new ResetPasswordRequest("validUser", "test1112", "ValidPassword123");

        // when, then
        mockMvc.perform(patch("/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RESET_PASSWORD_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(RESET_PASSWORD_SUCCESS.getMessage()));

        verify(memberService, times(1)).resetPasswordByEmailCode(request);
    }
}