package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.dto.*;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.jwt.CustomAuthorityUtils;
import com.project.Instagram.global.jwt.JwtTokenProvider;
import com.project.Instagram.global.jwt.RefreshTokenRedisRepository;
import com.project.Instagram.global.util.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.project.Instagram.global.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;
    @Mock
    CustomAuthorityUtils customAuthorityUtils;
    @Mock
    SecurityUtil securityUtil;
    @Mock
    RefreshTokenService refreshTokenService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    EmailAuthService emailAuthService;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    RefreshTokenRedisRepository refreshTokenRedisRepository;

    private final String DELETE_MEMBER_USERNAME = "--deleted--";

    @Nested
    class SendEmailConfirmation {
        @Test
        @DisplayName("이메일 인증 정상 동작 테스트")
        void validArgumentsSendEmail() {
            // given
            String email = RandomStringUtils.random(20, true, true) + "example.com";

            // when
            memberService.sendAuthEmail(email);

            // then
            then(emailAuthService).should().sendSignUpCode(email);
        }

        @Test
        @DisplayName("존재하는 이메일 예외 테스트")
        void emailExistThrowException() {
            // given
            String email = RandomStringUtils.random(20, true, true) + "example.com";
            given(memberRepository.existsByEmail(email)).willReturn(true);

            // when, then
            Assertions.assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> memberService.sendAuthEmail(email))
                    .withMessage(EMAIL_ALREADY_EXIST.getMessage());

            verify(emailAuthService, never()).sendSignUpCode(any());
        }
    }

    @Nested
    class UpdatePassword {
        @Test
        @DisplayName("비밀번호 업데이트 정상 동작 테스트")
        void updatePasswordWithValidPassword() {
            // given
            Member member = new Member();
            member.setPassword("oldPassword");
            UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "newPassword");

            // when
            when(securityUtil.getLoginMember()).thenReturn(member);
            when(bCryptPasswordEncoder.matches(request.getOldPassword(), member.getPassword())).thenReturn(true);
            memberService.updatePassword(request);

            // then
            then(memberRepository).should().save(member);
        }

        @Test
        @DisplayName("기존 비밀번호와 다른 경우 예외 테스트")
        void updatePasswordWithInvalidPassword() {
            // given
            Member member = new Member();
            member.setPassword("oldPassword");
            UpdatePasswordRequest request = new UpdatePasswordRequest("falsePassword", "newPassword");

            when(securityUtil.getLoginMember()).thenReturn(member);
            when(bCryptPasswordEncoder.matches(request.getOldPassword(), member.getPassword())).thenReturn(false);

            // when, then
            Assertions.assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> memberService.updatePassword(request))
                    .withMessage(PASSWORD_MISMATCH.getMessage());

            verify(memberRepository, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("기존 비밀번호와 새로운 비밀번호가 같은 경우 예외 테스트")
        void updatePasswordWithSamePassword() {
            // given
            Member member = new Member();
            member.setPassword("oldPassword");
            UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "oldPassword");

            when(securityUtil.getLoginMember()).thenReturn(member);
            when(bCryptPasswordEncoder.matches(request.getOldPassword(), member.getPassword())).thenReturn(true);

            // when, then
            Assertions.assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> memberService.updatePassword(request))
                    .withMessage(PASSWORD_SAME.getMessage());

            verify(memberRepository, never()).save(any(Member.class));
        }
    }

    @Nested
    class ResetPasswordByEmailCode {
        @Test
        @DisplayName("유효한 인증 코드와 새로운 비밀번호 재설정 성공 테스트")
        void resetPasswordWithValidCodeAndNewPassword() {
            // given
            ResetPasswordRequest request = new ResetPasswordRequest("username", "code", "newPassword");
            Member member = new Member();
            member.setUsername("username");
            member.setPassword("oldPassword");

            when(memberRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(member));
            when(emailAuthService.checkResetPasswordCode(request.getUsername(), request.getCode())).thenReturn(true);
            when(bCryptPasswordEncoder.matches(request.getNewPassword(), member.getPassword())).thenReturn(false);

            // when
            memberService.resetPasswordByEmailCode(request);

            // then
            then(memberRepository).should().save(member);
        }

        @Test
        @DisplayName("유효하지 않은 인증 코드로 비밀번호 재설정 실패 테스트")
        void resetPasswordWithInvalidCode() {
            // given
            ResetPasswordRequest request = new ResetPasswordRequest("username", "invalidCode", "newPassword");
            Member member = new Member();
            member.setUsername("username");

            when(memberRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(member));
            when(emailAuthService.checkResetPasswordCode(request.getUsername(), request.getCode())).thenReturn(false);

            // when, then
            Assertions.assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> memberService.resetPasswordByEmailCode(request))
                    .withMessage(PASSWORD_RESET_FAIL.getMessage());

            verify(memberRepository, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("현재 비밀번호와 새로운 비밀번호가 동일한 경우 실패 테스트")
        void resetPasswordWithSameOldAndNewPassword() {
            // given
            ResetPasswordRequest request = new ResetPasswordRequest("username", "Code", "oldPassword");
            Member member = new Member();
            member.setUsername("username");
            member.setPassword("oldPassword");

            when(memberRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(member));
            when(emailAuthService.checkResetPasswordCode(request.getUsername(), request.getCode())).thenReturn(true);
            when(bCryptPasswordEncoder.matches(request.getNewPassword(), member.getPassword())).thenReturn(true);

            // when, then
            Assertions.assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> memberService.resetPasswordByEmailCode(request))
                    .withMessage(PASSWORD_SAME.getMessage());

            verify(memberRepository, never()).save(any(Member.class));
        }
    }

    @Test
    @Transactional
    @DisplayName("[member] sign up new member:success")
    void test_sign_up_not_existing_member_success() {
        //given
        Optional<Member> member = Optional.empty();
        assertEquals(false, member.isPresent());
        given(memberRepository.findByUsername(Mockito.anyString())).willReturn(member);
        given(emailAuthService.checkSignUpCode(Mockito.anyString(), Mockito.anyString())).willReturn(true);
        given(memberRepository.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).willReturn(null);
        String username = "luee1004";
        String name = "haneul";
        String password = "lueepwd2023";
        String email = "yunide073@gmail.com";
        String code = "QLl6xq";
        SignUpRequest request = new SignUpRequest(username, name, password, email, code);

        //when
        boolean result = memberService.signUp(request);

        //then
        assertEquals(true, result);
    }

    @Test
    @DisplayName("[member] sign up restore member:success")
    void test_sign_up_existing_member_restore_success() {
        //given
        Optional<Member> member = Optional.empty();
        given(memberRepository.findByUsername(Mockito.anyString())).willReturn(member);
        given(emailAuthService.checkSignUpCode(Mockito.anyString(), Mockito.anyString())).willReturn(true);
        Member existingMember = new Member();
        given(memberRepository.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).willReturn(existingMember);
        String username = "luee1004";
        String name = "haneul";
        String password = "lueepwd2023";
        String email = "yunide073@gmail.com";
        String code = "QLl6xq";
        SignUpRequest request = new SignUpRequest(username, name, password, email, code);

        //when
        boolean result = memberService.signUp(request);

        //then
        assertEquals(result, true);
        verify(memberRepository, times(1)).findByUsername(Mockito.anyString());
        verify(emailAuthService, times(1)).checkSignUpCode(Mockito.anyString(), Mockito.anyString());
        verify(memberRepository, times(1)).findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    @DisplayName("[member] sign up existing username:fail")
    void test_sign_up_existing_username_throw_exception() {
        //given
        Optional<Member> member = Optional.of(new Member());
        assertEquals(member.isPresent(), true);
        given(memberRepository.findByUsername(Mockito.anyString())).willReturn(member);
        given(emailAuthService.checkSignUpCode(Mockito.anyString(), Mockito.anyString())).willReturn(true);
        String username = "luee1004";
        String name = "haneul";
        String password = "lueepwd2023";
        String email = "yunide073@gmail.com";
        String code = "QLl6xq";
        SignUpRequest request = new SignUpRequest(username, name, password, email, code);

        //when
        Throwable exception = assertThrows(BusinessException.class, () -> {
            memberService.signUp(request);
        });

        //then
        assertEquals(exception.getMessage(), USERNAME_ALREADY_EXIST.getMessage());
        verify(memberRepository, times(1)).findByUsername(Mockito.anyString());
        verify(emailAuthService, times(1)).checkSignUpCode(Mockito.anyString(), Mockito.anyString());

    }

    @Test
    @DisplayName("[member] update account:success")
    void test_update_account_success() {
        //given
        Member loginMember = new Member();
        loginMember.setUsername("notluee");
        loginMember.setPhone("010-1111-2222");
        given(securityUtil.getLoginMember()).willReturn(loginMember);
        given(memberRepository.existsByUsername(Mockito.anyString())).willReturn(false);

        String update_username = "luee";
        String name = "haneul";
        String link = "https://www.example.com";
        String introduce = "introduce-luee";
        String email = "luee@naver.com";
        String update_phone = "010-3333-4444";
        String gender = "MALE";
        UpdateAccountRequest request = new UpdateAccountRequest(update_username, name, link, introduce, email, update_phone, gender);
        //when
        memberService.updateAccount(request);

        //then
        assertEquals(loginMember.getUsername(), update_username);
        assertEquals(loginMember.getPhone(), update_phone);
    }

    @Test
    @DisplayName("[member] update account:success")
    void test_update_account_exist_username_throw_exception() {
        //given
        Member loginMember = new Member();
        loginMember.setUsername("luee");
        loginMember.setPhone("010-1111-2222");
        given(securityUtil.getLoginMember()).willReturn(loginMember);
        given(memberRepository.existsByUsername(Mockito.anyString())).willReturn(true);

        String update_username = "notluee";
        String name = "haneul";
        String link = "https://www.example.com";
        String introduce = "introduce-luee";
        String email = "luee@naver.com";
        String update_phone = "010-3333-4444";
        String gender = "MALE";
        UpdateAccountRequest request = new UpdateAccountRequest(update_username, name, link, introduce, email, update_phone, gender);

        //when
        Throwable exception = assertThrows(BusinessException.class, () -> {
            memberService.updateAccount(request);
        });

        //then
        assertEquals(exception.getMessage(), USERNAME_ALREADY_EXIST.getMessage());
        assertEquals(loginMember.getUsername(), "luee");
        assertEquals(loginMember.getPhone(), "010-1111-2222");
    }

    @Test
    @DisplayName("[member] delete:success")
    void test_delete_success() {
        //given
        Member loginMember = new Member();
        given(securityUtil.getLoginMember()).willReturn(loginMember);
        //when
        memberService.deleteMember();
        //then
        assertNotNull(loginMember.getDeletedAt());
        assertEquals(loginMember.getUsername(), DELETE_MEMBER_USERNAME);
    }

    @Test
    @DisplayName("[member] get profile page : success")
    void test_get_profile_page_success() {
        //given
        int page = 0;
        int size = 2;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Member> members = new ArrayList<>();
        members.add(new Member());
        members.add(new Member());
        members.add(new Member());
        members.add(new Member());
        members.add(new Member());
        members.add(new Member());
        members.add(new Member());
        Page<Member> memberPages = new PageImpl<>(members, pageRequest, members.size());
        given(memberRepository.findAllByDeletedAtIsNull(pageRequest)).willReturn(memberPages);

        //when
        PageListResponse<Profile> response = memberService.getProfilePageList(page, size);

        //then
        assertNotNull(response);
        assertEquals(size, response.getPageInfo().getSize());
        assertEquals(members.size(), response.getPageInfo().getTotalElements());
        assertEquals(members.get(0).getUsername(), response.getData().get(0).getUsername());
        assertEquals(page, response.getPageInfo().getPage() - 1);
        verify(memberRepository, times(1)).findAllByDeletedAtIsNull(pageRequest);
    }
    @Nested
    class sendCodeByEmail {
        @Test
        @DisplayName("이메일 인증 코드 동작 테스트")
        void validSendEamil(){
            //given
            String exUsername = "exex22";
            SendPasswordEmailRequest sendPasswordEmailRequest = new SendPasswordEmailRequest(exUsername);
            Member member = new Member();
            member.setUsername(exUsername);
            member.setEmail("test@example.com");

            //when
            when(memberRepository.findByUsername(exUsername)).thenReturn(Optional.of(member));
            memberService.sendPasswordCodeEmail(sendPasswordEmailRequest);
            //then
            then(emailAuthService).should().sendResetPasswordCode(exUsername,"test@example.com");

        }
        @Test
        @DisplayName("Username 존재 여부 예외 처리 테스트")
        void usernameNotExistThrowException(){
            String username = "exex11";
            SendPasswordEmailRequest sendPasswordEmailRequest = new SendPasswordEmailRequest(username);
            //when then
            when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> memberService.sendPasswordCodeEmail(sendPasswordEmailRequest))
                    .withMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }
    @Nested
    class logout {

        @Test
        @DisplayName("로그아웃 테스트")
        void validLogout(){
            //given
            String username = "exex333";
            Member member = new Member();
            member.setId(1L);
            member.setUsername(username);

            when(securityUtil.getLoginMember()).thenReturn(member);

            //when
            memberService.logout();
            //then
            verify(refreshTokenService).deleteRefreshTokenByValue(member.getId());
        }
    }
    @Nested
    class getProfile {

        @Test
        @DisplayName("getProfile 동작 테스트")
        void validGetProfile(){
            String username = "exex22";
            Member member = new Member();
            member.setUsername(username);
            member.setImage("testImage");
            member.setIntroduce("test,test1212");
            when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));
            //when
            Profile profile = memberService.getProfile(username);
            //then
            assertEquals(member.getUsername(),profile.getUsername());
            assertEquals(member.getImage(),profile.getImage());
            assertEquals(member.getIntroduce(),profile.getIntroduce());
            verify(memberRepository).findByUsername(username);
        }

        @Test
        @DisplayName("Username 존재 여부 예외 처리 테스트")
        void usernameNotExistThrowException(){
            String username = "exex11";

            when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> memberService.getProfile(username))
                    .withMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class reissueAccessToken {
        @Test
        @DisplayName("토큰 재발급 정상 처리 테스트")
        @WithMockUser(username = "exex22")
        void testReissueAccessToken() {
            String refreshToken = "ex_refresh_token";
            String access = "ex_access_token";
            Member member = new Member();
            member.setId(1L);
            member.setUsername("exex22");
            member.setName("사사사");
            member.setPassword("qwer1234");
            member.setEmail("exex1122@exex.com");
            Jws<Claims> mockJws = Mockito.mock(Jws.class);
            Claims mockClaims = Mockito.mock(Claims.class);


            when(securityUtil.getLoginMember()).thenReturn(member);
            when(jwtTokenProvider.generateAccessToken(anyMap(), anyString())).thenReturn(access);
            when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn(refreshToken);
            when(mockJws.getBody()).thenReturn(mockClaims);
            when(jwtTokenProvider.getClaims(Mockito.anyString())).thenReturn(mockJws);

            Map<String, String> result = memberService.reissueAccessToken(access, refreshToken);

            assertEquals(access, result.get("access"));
            assertEquals(refreshToken, result.get("refresh"));
            verify(refreshTokenService).deleteRefreshTokenByValue(member.getId());
            verify(refreshTokenService).saveRefreshTokenByValue(member.getId(), refreshToken);
        }

    }

}


