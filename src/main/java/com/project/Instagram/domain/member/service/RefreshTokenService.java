package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.entity.RefreshToken;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.jwt.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @SneakyThrows
    @Transactional
    public void deleteRefreshTokenByValue(Long memberId) {
        try {
            List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(memberId);
            for (RefreshToken refreshToken : refreshTokens)
                refreshTokenRedisRepository.delete(refreshToken);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.MEMBER_ID_REFRESH_TOKEN_DOES_NOT_EXIST);
        }
    }

    @SneakyThrows
    @Transactional
    public void saveRefreshTokenByValue(Long memberId, String refreshStr) {
        RefreshToken token=new RefreshToken(memberId, refreshStr);
        refreshTokenRedisRepository.save(token);
    }
}
