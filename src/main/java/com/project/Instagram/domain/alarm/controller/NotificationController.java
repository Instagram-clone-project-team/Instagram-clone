package com.project.Instagram.domain.alarm.controller;

import com.project.Instagram.domain.alarm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@PathVariable Long memberId) {
        return notificationService.connectNotification(memberId);
    }
}
