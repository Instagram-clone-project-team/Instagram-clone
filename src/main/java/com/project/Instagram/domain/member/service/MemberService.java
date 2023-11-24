package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.dto.*;
import com.project.Instagram.domain.member.entity.Gender;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.MemberRole;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.jwt.CustomAuthorityUtils;
import com.project.Instagram.global.jwt.JwtTokenProvider;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final CustomAuthorityUtils customAuthorityUtils;
    private final SecurityUtil securityUtil;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailAuthService emailAuthService;
    private final String DELETE_MEMBER_USERNAME="--deleted--";
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public boolean signUp(SignUpRequest signUpRequest) {
        Optional<Member> existingUsername = memberRepository.findByUsername(signUpRequest.getUsername());

        if (!emailAuthService.checkSignUpCode(signUpRequest.getEmail(), signUpRequest.getCode())) {
            return false;
        }

        if (existingUsername.isPresent()) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXIST);
        }

        Member existingMember = memberRepository.findByUsernameOrEmail(signUpRequest.getUsername(), signUpRequest.getEmail());
        if (existingMember != null && existingMember.getDeletedAt() != null) {
            restoreMembership(existingMember, signUpRequest);
        } else if (existingMember == null) {
            createNewMember(signUpRequest);
        }
        return true;
    }

    @Transactional
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest){
        Member member = securityUtil.getLoginMember();

        if(!bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(),member.getPassword())){
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
        if(updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getOldPassword())){
            throw new BusinessException(ErrorCode.PASSWORD_SAME);
        }
        final String password = bCryptPasswordEncoder.encode(updatePasswordRequest.getNewPassword());
        member.setEncryptedPassword(password);
        memberRepository.save(member);
    }

    private void createNewMember(SignUpRequest signUpRequest) {
        Set<MemberRole> roles = customAuthorityUtils.createRole(signUpRequest.getEmail());
        Member newMember = convertRegisterRequestToMember(signUpRequest, roles);
        String encryptedPassword = bCryptPasswordEncoder.encode(newMember.getPassword());
        newMember.setEncryptedPassword(encryptedPassword);
        memberRepository.save(newMember);
    }

    private void restoreMembership(Member existingMember, SignUpRequest signUpRequest) {
        existingMember.setDeletedAt(null);
        existingMember.setRestoreMembership(
                signUpRequest.getUsername(),
                bCryptPasswordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getName()
        );
        memberRepository.save(existingMember);
    }

    public void sendAuthEmail (String email){
        if (memberRepository.existsByEmail(email)) throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXIST);
        emailAuthService.sendSignUpCode(email);
    }

    private Member convertRegisterRequestToMember(SignUpRequest signUpRequest, Set<MemberRole> roles) {
        return Member.builder()
                .username(signUpRequest.getUsername())
                .name(signUpRequest.getName())
                .password(signUpRequest.getPassword())
                .email(signUpRequest.getEmail())
                .roles(roles)
                .build();
    }

    @Transactional
    public void updateAccount(UpdateAccountRequest updateAccountRequest) {
        Member member = securityUtil.getLoginMember();

        if(memberRepository.existsByUsername(updateAccountRequest.getUsername())
                && !member.getUsername().equals(updateAccountRequest.getUsername())){
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXIST);
        }

        updateMemberAccount(member,updateAccountRequest);

    }

    public void sendPasswordCodeEmail(SendPasswordEmailRequest sendPasswordEmailRequest) {
        final String username = sendPasswordEmailRequest.getUsername();
        final Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        String email = member.getEmail();
        emailAuthService.sendResetPasswordCode(username,email);
    }

    public void resetPasswordByEmailCode(ResetPasswordRequest resetPasswordRequest) {
        final Member member =memberRepository.findByUsername(resetPasswordRequest.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if(!emailAuthService.checkResetPasswordCode(member.getUsername(),resetPasswordRequest.getCode())){//이메일 인증 코드 비교
            throw new BusinessException(ErrorCode.PASSWORD_RESET_FAIL);
        }
        if(bCryptPasswordEncoder.matches(resetPasswordRequest.getNewPassword(), member.getPassword())){//현재 비밀번호, 새로운 비밀번호 비교
            throw new BusinessException(ErrorCode.PASSWORD_SAME);
        }

        final String newPassword = bCryptPasswordEncoder.encode(resetPasswordRequest.getNewPassword());
        member.setEncryptedPassword(newPassword);
        memberRepository.save(member);
    }

    public void updateMemberAccount(Member member, UpdateAccountRequest updateAccountRequest){
        member.updateUsername(updateAccountRequest.getUsername());
        member.updateName(updateAccountRequest.getName());
        member.updateLink(updateAccountRequest.getLink());
        member.updateIntroduce(updateAccountRequest.getIntroduce());
        member.updatePhone(updateAccountRequest.getPhone());
        member.updateEmail(updateAccountRequest.getEmail());
        member.updateGender(Gender.valueOf(updateAccountRequest.getGender()));
    }

    @Transactional
    public void logout() {
        refreshTokenService.deleteRefreshTokenByValue(securityUtil.getLoginMember().getId());
    }

    public Profile getProfile(String username){
        Member member=memberRepository.findByUsername(username).orElseThrow(()-> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return Profile.convertMemberToProfile(member);
    }

    public PageListResponse<Profile> getProfilePageList(int page, int size){
        Page<Member> pages = memberRepository.findAllByDeletedAtIsNull(PageRequest.of(page, size));
        List<Profile> profileList = pages.getContent()
                .stream()
                .map(Profile::convertMemberToProfile)
                .collect(Collectors.toList());
        return new PageListResponse(profileList, pages);
    }

    @Transactional
    public void deleteMember(){
        Member member=securityUtil.getLoginMember();
        member.updateUsername(DELETE_MEMBER_USERNAME);
        member.setDeletedAt(LocalDateTime.now());
    }

    public Map<String, String> reissueAccessToken(String access, String refresh){
        if(refresh.isEmpty()){
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXIST);
        }
        Member member=securityUtil.getLoginMember();

        jwtTokenProvider.verifySignature(refresh);
        String jws = access.replace("Bearer ", "");
        Map<String, Object> claims= jwtTokenProvider.getClaims(jws).getBody();
        String newAccessToken=jwtTokenProvider.generateAccessToken(claims, member.getEmail());
        String newRefreshToken=jwtTokenProvider.generateRefreshToken(member.getEmail());

        refreshTokenService.deleteRefreshTokenByValue(member.getId());
        refreshTokenService.saveRefreshTokenByValue(member.getId(), newRefreshToken);

        List<GrantedAuthority> authorities=new ArrayList<>();
        Set<MemberRole> set=member.getRoles();
        for(MemberRole role:set){
            authorities.add(new SimpleGrantedAuthority(role.toString()));
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getUsername(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, String> response=new HashMap<>();
        response.put("access", newAccessToken);
        response.put("refresh", newRefreshToken);
        return response;
    }

}
