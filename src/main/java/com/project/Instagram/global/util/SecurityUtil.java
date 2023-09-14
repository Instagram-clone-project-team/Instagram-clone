package com.project.Instagram.global.util;

import com.project.Instagram.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final MemberRepository memberRepository;

    public Long getLoginMemberId(){
        try {
            final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
            return Long.valueOf(memberId);
        } catch (Exception e) {
            throw new SecurityException("현재 사용자의 ID를 가져오는 중 문제가 발생했습니다.", e);
        }
    }
}
