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
public class SseService {
    private static final Long DEFAULT_TIMEOUT = 120L * 1000 * 60;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    @Transactional
    public SseEmitter connect(String username) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(username,emitter);

        emitter.onTimeout(() -> emitters.remove(username));
        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onError((e)->emitters.remove(username));
        sendToClient(username, "연결되었습니다. " + username + "님");
        return emitter;
    }

    public void sendToClient(String username, Object data){
        SseEmitter emitter = emitters.get(username);
        if(emitter != null){
            try{
                emitter.send(SseEmitter.event().id("Id : "+username).name("sse").data(data));
            }
            catch (IOException e){
                emitters.forEach((key, value) -> {
                    if (key.startsWith(username)) emitters.remove(key);
                });
                throw new BusinessException(NOTIFICATION_CONNECTION_ERROR);
            }
        }
    }
}
