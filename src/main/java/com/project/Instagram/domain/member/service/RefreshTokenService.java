package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Transactional
    public void deleteRefreshTokenByValue(Long memberId, String value) {
        final RefreshToken refreshToken = refreshTokenRedisRepository.findByMemberIdAndValue(memberId, value).orElseThrow(new AuthenticationException("로그인 유저가 아닙니다."));
        refreshTokenRedisRepository.delete(refreshToken);
    }
}
