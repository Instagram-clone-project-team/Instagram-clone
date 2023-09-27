package com.project.Instagram.global.jwt;

import com.project.Instagram.domain.member.entity.MemberRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomAuthorityUtils {
    @Value("${mail.address.admin}")
    private String adminMailAddress;

    public List<GrantedAuthority> createAuthorities(Set<MemberRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    public Set<MemberRole> createRole(String email) {
        if (email.equals(adminMailAddress)) {
            return new HashSet<>(Arrays.asList(MemberRole.ADMIN, MemberRole.USER));
        }
        return Collections.singleton(MemberRole.USER);
    }
}
