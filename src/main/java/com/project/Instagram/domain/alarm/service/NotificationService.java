package com.project.Instagram.domain.alarm.service;

import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.project.Instagram.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Transactional
    public SseEmitter connectNotification(String username) {
        securityUtil.checkLoginMember();
        memberRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        SseEmitter emitter = new SseEmitter();
        emitters.put(username, emitter);
        emitter.onTimeout(() -> emitters.remove(username));
        emitter.onCompletion(() -> emitters.remove(username));
        sendNotification(username, username + "님의 통신 연결이 완료되었습니다.");
        return emitter;
    }

        public void sendNotification(String username, String message) {
        SseEmitter emitter = emitters.get(username);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(username);
                throw new BusinessException(NOTIFICATION_CONNECTION_ERROR);
            }
        }
    }
}
