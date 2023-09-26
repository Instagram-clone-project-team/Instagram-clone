package com.project.Instagram.global.jwt;

import com.project.Instagram.domain.member.entity.MemberRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthorityUtils {
    @Value("${mail.address.admin}")
    private String adminMailAddress;

    private final List<MemberRole> ADMIN_ROLES = List.of(MemberRole.ROLE_ADMIN, MemberRole.ROLE_USER);
    private final List<MemberRole> USER_ROLES = List.of(MemberRole.ROLE_USER);

    public List<GrantedAuthority> createAuthorities(List<MemberRole> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());
        return authorities;
    }

    public List<MemberRole> createRole(String email) {
        if (email.equals(adminMailAddress)) {
            return ADMIN_ROLES;
        }
        return USER_ROLES;
    }

}
