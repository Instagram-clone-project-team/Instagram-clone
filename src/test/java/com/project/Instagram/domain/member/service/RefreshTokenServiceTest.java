package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.RefreshToken;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.jwt.RefreshTokenRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.project.Instagram.global.error.ErrorCode.MEMBER_ID_REFRESH_TOKEN_DOES_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @InjectMocks
    RefreshTokenService refreshTokenService;
    @Mock
    RefreshTokenRedisRepository refreshTokenRedisRepository;


    @Test
    @DisplayName("리프레쉬 토큰 삭제 테스트")
    void deleteRefreshTokenByValue() {
        String username = "exex333";
        Member member = new Member();
        member.setId(1L);
        member.setUsername(username);
        List<RefreshToken> list= new ArrayList<>();
        RefreshToken refreshToken1 = RefreshToken.builder()
                .memberId(member.getId())
                .value("refreshTokenValue")
                .build();
        RefreshToken refreshToken2 = new RefreshToken();

        list.add(refreshToken1);
        list.add(refreshToken2);

        when(refreshTokenRedisRepository.findAllByMemberId(member.getId())).thenReturn(list);
        //when
        refreshTokenService.deleteRefreshTokenByValue(member.getId());
        //then
        verify(refreshTokenRedisRepository,times(2)).delete(any(RefreshToken.class));
    }
    @Test
    @DisplayName("리프레쉬 토큰 없이 로그아웃 테스트")
    @WithMockUser(username = "exex22")
    void refreshNotExistThrowException(){
        String username = "exex333";
        Member member = new Member();
        member.setId(1L);
        member.setUsername(username);
        List<RefreshToken> list= new ArrayList<>();
        RefreshToken refreshToken1 = RefreshToken.builder()
                .memberId(member.getId())
                .value("refreshTokenValue")
                .build();
        RefreshToken refreshToken2 = new RefreshToken();

        list.add(refreshToken1);
        list.add(refreshToken2);

        when(refreshTokenRedisRepository.findAllByMemberId(member.getId())).thenReturn(Collections.emptyList());
        // when, then
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> refreshTokenService.deleteRefreshTokenByValue(member.getId()))
                .withMessage(MEMBER_ID_REFRESH_TOKEN_DOES_NOT_EXIST.getMessage());

    }
}