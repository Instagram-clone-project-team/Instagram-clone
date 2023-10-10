package com.project.Instagram.global.util;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final MemberRepository memberRepository;

    public Member getLoginMember() {
        Optional<Member> findMember = memberRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        findMember.orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_INFORMATION_ERROR));
        return findMember.get();
    }
    public void checkLoginMember(){
        boolean checkMember = memberRepository.existsByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!checkMember){
            throw new BusinessException(ErrorCode.MEMBER_NOT_LOGIN);
        }
    }
}
