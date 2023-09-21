package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.dto.SignUpRequest;
import com.project.Instagram.domain.member.dto.UpdateAccountRequest;
import com.project.Instagram.domain.member.dto.UpdatePasswordRequest;
import com.project.Instagram.domain.member.entity.Gender;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;

import static com.project.Instagram.domain.member.entity.Gender.MALE;
import static com.project.Instagram.domain.member.entity.MemberRole.ROLE_USER;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailAuthService emailAuthService;

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
}
