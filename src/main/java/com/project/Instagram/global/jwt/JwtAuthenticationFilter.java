package com.project.Instagram.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Instagram.domain.member.dto.LoginRequest;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        return authenticationManager.authenticate(authenticationToken);
    }

    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        Member member = (Member) authResult.getPrincipal();

        String accessToken = delegateAccessToken(member);
        String refreshToken = delegateRefreshToken(member);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);
    }
    private String delegateAccessToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getUsername());
        claims.put("roles", member.getRoles());
        String subject = member.getUsername();
        return jwtTokenProvider.generateAccessToken(claims, subject);
    }

    private String delegateRefreshToken(Member member) {
        String subject = member.getUsername();
        final String refreshToken =  jwtTokenProvider.generateRefreshToken(subject);
        redisSave(member, refreshToken);
        return refreshToken;
    }

    private void redisSave(Member member, String refreshToken) {
        final RefreshToken reidsRefreshToken = RefreshToken.builder()
                .memberId(member.getId())
                .value(refreshToken)
                .build();
        refreshTokenRedisRepository.save(reidsRefreshToken);
    }
}
