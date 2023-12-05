package com.project.Instagram.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.member.dto.*;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.member.service.MemberService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {
    @Autowired
    ObjectMapper jsonMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    MemberService memberService;

    @DisplayName("sign up : success")
    @WithMockUser
    @Test
    void test_signUp_success() throws Exception {
        //given
        String username = "luee1004";
        String name = "haneul";
        String password = "lueepwd2023";
        String email = "yunide073@gmail.com";
        String code = "QLl6xq";
        SignUpRequest request = new SignUpRequest(username, name, password, email, code);
        given(memberService.signUp(Mockito.any())).willReturn(true);

        //when, then
        mvc.perform(post("/accounts/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SIGNUP_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(SIGNUP_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(print());

        verify(memberService).signUp(Mockito.any());
    }

    @DisplayName("sign up : fail")
    @WithMockUser
    @Test
    void test_signUp_fail() throws Exception {
        //given
        String username = "luee1004";
        String name = "haneul";
        String password = "lueepwd2023";
        String email = "yunide073@gmail.com";
        String code = "QLl6xq";
        SignUpRequest request = new SignUpRequest(username, name, password, email, code);
        given(memberService.signUp(Mockito.any())).willReturn(false);

        //when, then
        mvc.perform(post("/accounts/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(EMAIL_VERIFICATION_FAIL.getStatus()))
                .andExpect(jsonPath("$.message").value(EMAIL_VERIFICATION_FAIL.getMessage()))
                .andExpect(jsonPath("$.data").value(false))
                .andDo(print());

        verify(memberService).signUp(Mockito.any());
    }

    @DisplayName("update account : success")
    @WithMockUser
    @Test
    void updateAccount() throws Exception {
        //given
        String username = "luee";
        String name = "haneul";
        String link = "https://www.example.com";
        String introduce = "introduce-luee";
        String email = "luee@naver.com";
        String phone = "010-1111-2222";
        String gender = "MALE";
        UpdateAccountRequest request = new UpdateAccountRequest(username, name, link, introduce, email, phone, gender);

        //when, then
        mvc.perform(patch("/account/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(UPDATE_ACCOUNT_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(UPDATE_ACCOUNT_SUCCESS.getMessage()))
                .andDo(print());

        verify(memberService).updateAccount(Mockito.any());
    }

    @Test
    @WithMockUser
    void test_delete() throws Exception {
        mvc.perform(delete("/member")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(DELETE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(DELETE_SUCCESS.getMessage()))
                .andDo(print());

        verify(memberService).deleteMember();
    }

    @DisplayName("get profiles page : success")
    @Test
    @WithMockUser
    void getProfilesPage() throws Exception {
        //given
        Member member1 = new Member();
        Member member2 = new Member();
        Member member3 = new Member();
        List<Profile> data = new ArrayList<>();
        data.add(new Profile(member1));
        data.add(new Profile(member2));
        data.add(new Profile(member3));
        Page<Profile> pageInfo = new PageImpl<>(data, PageRequest.of(0, 2), data.size());
        PageListResponse<Profile> pageList = new PageListResponse(data, pageInfo);
        given(memberService.getProfilePageList(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageList);

        //when, then
        mvc.perform(get("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(LOOK_UP_MEMBER_LIST_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(LOOK_UP_MEMBER_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.pageInfo.page").value("1"))
                .andExpect(jsonPath("$.data.pageInfo.size").value("2"))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value("3"))
                .andDo(print());

        verify(memberService).getProfilePageList(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @WithMockUser
    @DisplayName("이메일 인증 코드 앤드포인트 동작 테스트")
    void sendAuthCodeByEmail() throws Exception {
        // given
        String email = "test@example.com";

        doNothing().when(memberService).sendAuthEmail(email);

        // when, then
        mvc.perform(post("/accounts/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(jsonMapper.writeValueAsString(email)))
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
        mvc.perform(patch("/password/patch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(jsonMapper.writeValueAsString(request)))
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
        mvc.perform(patch("/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RESET_PASSWORD_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.message").value(RESET_PASSWORD_SUCCESS.getMessage()));

        verify(memberService, times(1)).resetPasswordByEmailCode(request);
    }
    @Test
    @DisplayName("비밀번호인증코드 이메일 보내기 테스트")
    @WithMockUser
    public void sendPasswordCodeByEmailTest() throws Exception{
        SendPasswordEmailRequest sendPasswordEmailRequest = new SendPasswordEmailRequest("exex22");

        mvc.perform(
                        post("/password/reset/email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(jsonMapper.writeValueAsString(sendPasswordEmailRequest)))
                .andExpect(status().isOk());

        verify(memberService).sendPasswordCodeEmail(sendPasswordEmailRequest);
    }
    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser
    public void logoutTest() throws Exception{
        mvc.perform(
                        delete("/token/delete")
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

        mvc.perform(
                        get("/member/{username}",username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(jsonMapper.writeValueAsString(username)))
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

        mvc.perform(
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