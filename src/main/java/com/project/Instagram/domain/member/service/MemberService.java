package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.dto.SignUpRequest;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailAuthService emailAuthService;
    private final SecurityUtil securityUtil;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public boolean signUp(SignUpRequest signUpRequest) {
        if (memberRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new EntityExistsException("해당 사용자 이름이 이미 존재합니다.");
        }

        final String username = signUpRequest.getUsername();

        if (!emailAuthService.checkSignUpCode(username, signUpRequest.getEmail(), signUpRequest.getCode())) {
            return false;
        }

        final Member member = convertRegisterRequestToMember(signUpRequest);
        final String encryptedPassword = bCryptPasswordEncoder.encode(member.getPassword());
        member.setEncryptedPassword(encryptedPassword);
        memberRepository.save(member);

        return true;
    }

    public void sendAuthEmail(String username, String email) {
        if (memberRepository.existsByUsername(username)) {
            throw new EntityExistsException("해당 사용자 이름이 이미 존재합니다.");
        }
        emailAuthService.sendSignUpCode(username, email);
    }

    private Member convertRegisterRequestToMember(SignUpRequest signUpRequest) {
        return Member.builder()
                .username(signUpRequest.getUsername())
                .name(signUpRequest.getName())
                .password(signUpRequest.getPassword())
                .email(signUpRequest.getEmail())
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshTokenByValue(securityUtil.getLoginMemberId(), refreshToken);
    }
}
