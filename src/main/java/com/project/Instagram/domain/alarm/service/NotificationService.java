package com.project.Instagram.domain.alarm.service;

import com.project.Instagram.global.error.BusinessException;
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

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Transactional
    public SseEmitter connectNotification(Long memberId) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(memberId, emitter);
        emitter.onTimeout(() -> emitters.remove(memberId));
        emitter.onCompletion(() -> emitters.remove(memberId));
        return emitter;
    }

        public void sendNotification(Long memberId, String message) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(memberId);
                throw new BusinessException(NOTIFICATION_CONNECTION_ERROR);
            }
        }
    }
}
