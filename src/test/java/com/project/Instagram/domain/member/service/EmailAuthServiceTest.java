package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.repository.SignUpCodeRedisRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EmailAuthServiceTest {

    @InjectMocks
    private EmailAuthService emailAuthService;
    @Mock
    private SignUpCodeRedisRepository signUpCodeRedisRepository;
    @Mock
    private EmailService emailService;

    @Nested
    class SendSignUpCode {
        @Test
        @DisplayName("이메일 인증 동작 테스트")
        void sendSignUpCode() {
            // given
            final String email = RandomStringUtils.random(20, true, true) + "email.com";
            ReflectionTestUtils.invokeMethod(emailAuthService, "loadEmailUI");

            // when
            emailAuthService.sendSignUpCode(email);

            // then
            then(signUpCodeRedisRepository).should().save(any());
        }
    }
}