package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.dto.SignUpRequest;
import com.project.Instagram.domain.member.dto.UpdatePasswordRequest;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;

import java.util.Optional;

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
        Optional<Member> existingUsername = memberRepository.findByUsername(signUpRequest.getUsername());
        Optional<Member> existingEmail = memberRepository.findByEmail(signUpRequest.getEmail());

        if (!emailAuthService.checkSignUpCode(signUpRequest.getEmail(), signUpRequest.getCode())) {
            return false;
        }

        Member existingMember = memberRepository.findByUsernameOrEmail(signUpRequest.getUsername(), signUpRequest.getEmail());

        if (existingMember != null) {
            if (existingMember.getDeletedAt() != null) {
                if (!existingMember.getEmail().equals(signUpRequest.getEmail())) {
                    throw new EntityExistsException("해당 사용자 이름이 이미 존재합니다.");
                }
                restoreMembership(existingMember, signUpRequest);
            } else {
                throw new EntityExistsException("해당 사용자 이름이 이미 존재합니다.");
            }
        } else {
            if (existingUsername.isPresent()) {
                throw new EntityExistsException("해당 사용자 이름이 이미 존재합니다.");
            }

            if (existingEmail.isPresent()) {
                throw new EntityExistsException("이미 존재하는 이메일 주소입니다.");
            }
            createNewMember(signUpRequest);
        }
        return true;
    }

    @Transactional
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest){
//        //로그인 로직(추후 로그인 구현후 쓰임)
        Member member = new Member(1L, "yapped",ROLE_USER,"zzzzz3zzzzkkkk412",
                "엽엽이","www.naver.com","안녕하세요","dlduq29@gmail.com","010-2222-2222",MALE);


        member.setEncryptedPassword(bCryptPasswordEncoder.encode(member.getPassword()));
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
}