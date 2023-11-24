package com.project.Instagram.service;

import com.project.Instagram.domain.member.dto.SendPasswordEmailRequest;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.MemberRole;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.member.entity.RefreshToken;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.member.service.EmailAuthService;
import com.project.Instagram.domain.member.service.EmailService;
import com.project.Instagram.domain.member.service.MemberService;
import com.project.Instagram.domain.member.service.RefreshTokenService;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.jwt.JwtTokenProvider;
import com.project.Instagram.global.jwt.RefreshTokenRedisRepository;
import com.project.Instagram.global.util.SecurityUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.MEMBER_ID_REFRESH_TOKEN_DOES_NOT_EXIST;
import static com.project.Instagram.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MemberSerivceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private EmailAuthService emailAuthService;
    @Mock
    private RefreshTokenRedisRepository refreshTokenRedisRepository;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;


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
        @WithMockUser(username = "exex333")
        void validLogout(){
            //given
            String username = "exex333";
            Member member = new Member();
            member.setId(1L);
            member.setUsername(username);
            RefreshToken refreshToken = RefreshToken.builder()
                    .memberId(member.getId())
                    .value("refreshTokenValue")
                    .build();
            when(securityUtil.getLoginMember()).thenReturn(member);

            //when
            refreshTokenRedisRepository.save(refreshToken);
            memberService.logout();

            //then
            verify(refreshTokenService).deleteRefreshTokenByValue(member.getId());
            List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(member.getId());
            assertTrue(refreshTokens.isEmpty());
        }
        @Test
        @DisplayName("리프레쉬 토큰 없이 로그아웃 테스트")
        @WithMockUser(username = "exex22")
        void refreshNotExistThrowException(){
            String username = "exex333";
            Member member1 = new Member();
            member1.setId(1L);
            member1.setUsername(username);
            when(securityUtil.getLoginMember()).thenReturn(member1);
//            when(refreshTokenRedisRepository.findAllByMemberId(member1.getId())).thenReturn(Collections.emptyList());

            memberService.logout();
            // when, then
            verify(refreshTokenRedisRepository, never()).delete(any());
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> memberService.logout())
                    .withMessage(MEMBER_ID_REFRESH_TOKEN_DOES_NOT_EXIST.getMessage());
            //애초에 빈 list를 반환하기 때문에 반복문이 실행이 안된다. 그래서 예외처리가 안됨. 그럼 이유가 뭘까?
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

            when(securityUtil.getLoginMember()).thenReturn(member);

            Map<String, String> test =memberService.reissueAccessToken(access,refreshToken);

            assertThat(test)
                    .isNotEmpty()
                    .containsKey("access")
                    .containsKey("refresh");
        }
    }

}
