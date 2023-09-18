package com.project.Instagram.domain.member.service;

import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final String ENCODING_UTF8 = "UTF-8";
    private final JavaMailSender javaMailSender;

    @SneakyThrows
    @Async
    public void sendHtmlTextEmail(String subject, String content, String email) {
        final MimeMessage message = javaMailSender.createMimeMessage();

        try {
            final MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, ENCODING_UTF8);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            javaMailSender.send(message);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }
}
