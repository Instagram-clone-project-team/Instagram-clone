package com.project.Instagram.domain.member.service;

import com.project.Instagram.domain.member.entity.ResetPasswordCode;
import com.project.Instagram.domain.member.entity.SignUpCode;
import com.project.Instagram.domain.member.repository.ResetPasswordCodeRedisRepository;
import com.project.Instagram.domain.member.repository.SignUpCodeRedisRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private static final int SIGNUP_CODE_LENGTH = 6;
    private static final int RESET_PASSWORD_CODE_LENGTH = 8;

    private static final String SIGNUP_EMAIL_SUBJECT_POSTFIX = ", Welcome to Instagram";
    private static final String RESET_PASSWORD_EMAIL_SUBJECT_POSTFIX = ", recover your account's password.";
    private final EmailService emailService;
    private final SignUpCodeRedisRepository signUpCodeRedisRepository;
    private final ResetPasswordCodeRedisRepository resetPasswordCodeRedisRepository;

    private String confirEmailUI;

    public void sendSignUpCode(String email) {
        final String code = createEmailVerificationCode(SIGNUP_CODE_LENGTH);
        emailService.sendHtmlTextEmail(SIGNUP_EMAIL_SUBJECT_POSTFIX, getSignUpEmailText(email, code), email);

        final SignUpCode signUpCode = SignUpCode.builder()
                .email(email)
                .code(code)
                .build();
        signUpCodeRedisRepository.save(signUpCode);
    }

    public boolean checkSignUpCode(String email, String code) {
        final SignUpCode signUpCode = signUpCodeRedisRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.MEMBERSHIP_CODE_NOT_FOUND));

        if (!signUpCode.getCode().equals(code) || !signUpCode.getEmail().equals(email)) throw new BusinessException(ErrorCode.MEMBERSHIP_CODE_DOES_NOT_MATCH_EMAIL);;

        signUpCodeRedisRepository.delete(signUpCode);
        return true;
    }
    @SneakyThrows
    public void sendResetPasswordCode(String username, String email){

        final String code = createEmailVerificationCode(RESET_PASSWORD_CODE_LENGTH);
        final String text = getSignUpEmailText(email,code);
        emailService.sendHtmlTextEmail(username + RESET_PASSWORD_EMAIL_SUBJECT_POSTFIX,text,email);

        final ResetPasswordCode resetPasswordCode =ResetPasswordCode.builder()
                .username(username)
                .code(code)
                .build();
        resetPasswordCodeRedisRepository.save(resetPasswordCode);
    }
    @SneakyThrows
    public boolean checkResetPasswordCode(String username, String code){
        final ResetPasswordCode resetPasswordCode = resetPasswordCodeRedisRepository.findByUsername(username)
                .orElseThrow(() ->new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if(!resetPasswordCode.getCode().equals(code)) return false;

        resetPasswordCodeRedisRepository.delete(resetPasswordCode);

        return true;
    }

    private String getSignUpEmailText(String email, String code) {
        return String.format(confirEmailUI, email, code, email);
    }


    private String createEmailVerificationCode(int length) {
        return RandomStringUtils.random(length, true, true);
    }


    @SneakyThrows
    @PostConstruct
    private void loadEmailUI() {
        try {
            final ClassPathResource confirmEmailUIResource = new ClassPathResource("confirmEmailUI.html");
            confirEmailUI = new String(confirmEmailUIResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_CONVERT_FAIL);
        }
    }

}
