package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.dto.ResetPasswordRequest;
import com.project.Instagram.domain.member.dto.UpdateAccountRequest;
import com.project.Instagram.domain.member.dto.UpdatePasswordRequest;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.jwt.CustomAuthorityUtils;
import com.project.Instagram.global.util.SecurityUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private EmailAuthService emailAuthService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private CustomAuthorityUtils customAuthorityUtils;

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

}


