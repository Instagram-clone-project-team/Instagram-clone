package com.project.Instagram.domain.member.service;
import com.project.Instagram.domain.member.dto.*;
import com.project.Instagram.domain.member.entity.Gender;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final SecurityUtil securityUtil;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailAuthService emailAuthService;

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
//        //로그인 로직(추후 로그인 구현후 쓰임)
        Member member = memberRepository.findByUsername(updatePasswordRequest.getUsername())
                .orElseThrow(() ->new BusinessException(ErrorCode.MEMBER_NOT_FOUND));


        if(!bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(),member.getPassword())){//요청 비밀번호 현재 비밀번호 매치 확인
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
        Member newMember = convertRegisterRequestToMember(signUpRequest);
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
        emailAuthService.sendSignUpCode(email);
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
    public void updateAccount(UpdateAccountRequest updateAccountRequest) {
        //        //로그인 로직(추후 로그인 구현후 쓰임)
        Member member = memberRepository.findByUsername(updateAccountRequest.getUsername())
                .orElseThrow(() ->new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

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
}
